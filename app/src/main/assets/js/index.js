// BGM
    const bgm = document.getElementById("bgm");
    bgm.volume = 0.20;

    async function startBgm(){
      if(!bgm || !bgm.paused) return;

      try{
        await bgm.play();
      }catch(e){
        // Chrome may block autoplay until the first user interaction.
      }
    }

    // Try immediately and once more after the page is fully loaded.
    startBgm();
    window.addEventListener("load", () => {
      setTimeout(startBgm, 800);
    });

    // If autoplay is blocked, start on the first remote/key/mouse operation.
    ["pointerdown", "keydown", "touchstart"].forEach(eventName => {
      document.addEventListener(eventName, startBgm, { once:true });
    });

    const cards = [...document.querySelectorAll(".app-card")];

    // UI効果音
    const hoverSfx = document.getElementById("hoverSfx");
    const selectSfx = document.getElementById("selectSfx");
    let lastHoverSoundAt = 0;

    function playUiSound(audio, volume = 1.0) {
      if (!audio) return;

      try {
        audio.pause();
        audio.currentTime = 0;
        audio.volume = volume;
        const p = audio.play();
        if (p && typeof p.catch === "function") {
          p.catch(() => {});
        }
      } catch (e) {}
    }

    function playHoverSound() {
      const now = performance.now();

      // mouseenter と focus が連続した時の二重再生を防止
      if (now - lastHoverSoundAt < 90) return;
      lastHoverSoundAt = now;

      playUiSound(hoverSfx, 0.35);
    }

    function playSelectSound() {
      playUiSound(selectSfx, 0.55);
    }

    cards.forEach((card) => {
      card.addEventListener("mouseenter", playHoverSound);

      card.addEventListener("focus", () => {
        playHoverSound();
      });

      // 決定音
      card.addEventListener("click", () => {
        playSelectSound();
      });
    });

    const serviceName = document.getElementById("serviceName");
    const serviceDesc = document.getElementById("serviceDesc");
    let current = 0;

    function selectCard(index, focus = true){
      current = (index + cards.length) % cards.length;

      cards.forEach((card, i) => {
        card.classList.toggle("active", i === current);
      });

      const card = cards[current];
      serviceName.textContent = card.dataset.name || "";
      serviceDesc.textContent = card.dataset.desc || "";

      if(focus) card.focus({preventScroll:true});
    }

    cards.forEach((card, i) => {
      card.addEventListener("focus", () => selectCard(i, false));
      card.addEventListener("mouseenter", () => selectCard(i, false));
    });

    function updateClock(){
      const now = new Date();
      document.getElementById("clock").textContent =
        now.toLocaleTimeString("ja-JP", {
          hour:"2-digit",
          minute:"2-digit"
        });
    }

    updateClock();
    setInterval(updateClock, 1000);

    document.getElementById("fullscreenBtn").addEventListener("click", async () => {
      try{
        if(!document.fullscreenElement){
          await document.documentElement.requestFullscreen();
        }else{
          await document.exitFullscreen();
        }
      }catch(e){}
    });


    // 起動直後のキーボードフォーカスを確実にする
    function ensureLauncherFocus(force = false){
      const active = document.activeElement;
      const cardHasFocus = cards.includes(active);

      if (!force && cardHasFocus) return;

      try {
        window.focus();
        cards[current].focus({ preventScroll: true });
      } catch (e) {}
    }

    // 見た目だけでなく、最初のカードへ実際にフォーカス
    selectCard(0, true);

    // Windowsログオン直後はChromeが完全に前面になるまで
    // 少し時間がかかることがあるため、数回だけ再確認
    window.addEventListener("load", () => {
      setTimeout(() => ensureLauncherFocus(true), 150);
      setTimeout(() => ensureLauncherFocus(true), 700);
      setTimeout(() => ensureLauncherFocus(true), 1500);
    });

    // TV Launcherへ戻ってきた時もカード操作へ復帰
    window.addEventListener("focus", () => {
      setTimeout(() => {
        const active = document.activeElement;
        if (
          active === document.body ||
          active === document.documentElement ||
          active === null
        ) {
          ensureLauncherFocus(true);
        }
      }, 50);
    });

    // R18 簡易パスワードロック
    const r18Link = document.getElementById("r18Link");
    const R18_PASSWORD = "1234"; // ← 好きな4桁に変更

    if (r18Link) {
      r18Link.addEventListener("click", (e) => {
        if (sessionStorage.getItem("r18Unlocked") === "1") {
          return;
        }

        e.preventDefault();

        const input = prompt("R18コンテンツです。\n4桁のパスワードを入力してください。");

        if (input === null) return;

        if (input === R18_PASSWORD) {
          sessionStorage.setItem("r18Unlocked", "1");
          Android.openExternal(r18Link.href);
        } else {
          alert("パスワードが違います。");
        }
      });
    }


    // LUXURY / HOTEL VOD テーマ切替
    const themeBtn = document.getElementById("themeBtn");
    const themeRoot = document.documentElement;

    function currentTheme(){
      return themeRoot.getAttribute("data-launcher-theme") === "hotel" ? "hotel" : "luxury";
    }

    function updateThemeButton(){
      if (!themeBtn) return;
      themeBtn.textContent = currentTheme() === "hotel" ? "MODE: HOTEL VOD" : "MODE: LUXURY";
    }

    function setLauncherTheme(theme, save = true){
      const next = theme === "hotel" ? "hotel" : "luxury";
      themeRoot.setAttribute("data-launcher-theme", next);
      if (save) {
        try { localStorage.setItem("tvLauncherTheme", next); } catch (e) {}
      }
      updateThemeButton();
      playHoverSound();
    }

    function toggleLauncherTheme(){
      setLauncherTheme(currentTheme() === "luxury" ? "hotel" : "luxury");
    }

    updateThemeButton();
    if (themeBtn) themeBtn.addEventListener("click", toggleLauncherTheme);

    // USBリモコン/キーボードでも切替可能
    document.addEventListener("keydown", (e) => {
      if (e.key && e.key.toLowerCase() === "m" && !e.ctrlKey && !e.altKey && !e.metaKey) {
        e.preventDefault();
        toggleLauncherTheme();
        ensureLauncherFocus(true);
      }
    });


// SafeLite smartphone remote: page-local commands only.
window.TVRemoteCommand = function(action) {
  startBgm();

  switch (action) {
    case "left":
    case "up":
      selectCard(current - 1);
      break;

    case "right":
    case "down":
      selectCard(current + 1);
      break;

    case "enter":
      if (cards[current]) cards[current].click();
      break;

    case "home":
      selectCard(0);
      break;

    case "back":
      if (history.length > 1) history.back();
      break;

    case "theme":
      toggleLauncherTheme();
      ensureLauncherFocus(true);
      break;

    case "fullscreen":
      document.getElementById("fullscreenBtn")?.click();
      break;

    case "reload":
      location.reload();
      break;

    case "play_pause":
      if (bgm) {
        if (bgm.paused) bgm.play().catch(()=>{});
        else bgm.pause();
      }
      break;
  }
};
