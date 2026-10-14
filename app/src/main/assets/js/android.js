(() => {
  window.open = url => { Android.openExternal(String(url)); return null; };
  document.addEventListener('click', e => {
    const a=e.target.closest('a');
    if(!a || e.defaultPrevented) return;
    if(/^https?:/.test(a.href) && !a.href.startsWith('https://appassets.androidplatform.net/')) {
      e.preventDefault(); Android.openExternal(a.href);
    }
  });
  window.nativeKey = key => {
    const els=[...document.querySelectorAll('a[href],button')].filter(e=>e.offsetWidth&&e.offsetHeight);
    let active=document.activeElement;
    if(key==='Enter'){if(els.includes(active))active.click();else els[0]?.focus();return;}
    if(!els.includes(active)){els[0]?.focus();return;}
    const r=active.getBoundingClientRect(), x=r.left+r.width/2, y=r.top+r.height/2;
    let best=null,score=Infinity;
    els.filter(e=>e!==active).forEach(e=>{
      const b=e.getBoundingClientRect(),dx=b.left+b.width/2-x,dy=b.top+b.height/2-y;
      const horizontal=key==='ArrowLeft'||key==='ArrowRight';
      const primary=key==='ArrowLeft'?-dx:key==='ArrowRight'?dx:key==='ArrowUp'?-dy:dy;
      const cross=Math.abs(horizontal?dy:dx);
      const s=primary+cross*3;
      if(primary>8&&s<score){score=s;best=e;}
    });
    if(best){best.focus({preventScroll:true});best.scrollIntoView({block:'nearest',inline:'nearest'});}
  };
  document.addEventListener('keydown',e=>{if(['ArrowUp','ArrowDown','ArrowLeft','ArrowRight','Enter'].includes(e.key)){e.preventDefault();window.nativeKey(e.key);}});
})();
