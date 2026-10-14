package jp.privatesuite.launcher;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.res.Configuration;
import android.net.Uri;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainActivity extends Activity {
 private WebView web;
 private static final String BASE="https://appassets.androidplatform.net/";
 private static final int VIDEO=1, PLAYLIST=2;
 private boolean foreground;
 @Override public void onCreate(Bundle state) {
  super.onCreate(state);
  if((getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK)==Configuration.UI_MODE_TYPE_TELEVISION)
   setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  web=new WebView(this); setContentView(web);
  WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);
  s.setAllowFileAccess(false);s.setAllowContentAccess(false);s.setMediaPlaybackRequiresUserGesture(false);
  s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
  web.addJavascriptInterface(new Bridge(),"Android");
  web.setWebChromeClient(new WebChromeClient(){
   @Override public boolean onJsAlert(WebView v,String u,String message,JsResult r){
    new AlertDialog.Builder(MainActivity.this).setMessage(message).setPositiveButton("OK",(d,w)->r.confirm()).setOnCancelListener(d->r.cancel()).show();return true;
   }
   @Override public boolean onJsConfirm(WebView v,String u,String message,JsResult r){
    new AlertDialog.Builder(MainActivity.this).setMessage(message).setPositiveButton("OK",(d,w)->r.confirm()).setNegativeButton("キャンセル",(d,w)->r.cancel()).setOnCancelListener(d->r.cancel()).show();return true;
   }
   @Override public boolean onJsPrompt(WebView v,String u,String message,String value,JsPromptResult r){
    EditText input=new EditText(MainActivity.this);input.setSingleLine(true);input.setText(value);
    input.setInputType(18);
    new AlertDialog.Builder(MainActivity.this).setMessage(message).setView(input).setPositiveButton("OK",(d,w)->r.confirm(input.getText().toString())).setNegativeButton("キャンセル",(d,w)->r.cancel()).setOnCancelListener(d->r.cancel()).show();return true;
   }
  });
  web.setWebViewClient(new WebViewClient(){
   @Override public WebResourceResponse shouldInterceptRequest(WebView view,WebResourceRequest req){
    Uri u=req.getUrl();
    if(!"https".equals(u.getScheme())||!"appassets.androidplatform.net".equals(u.getHost()))return null;
    String p=u.getPath();if(p==null||p.contains(".."))return empty();
    try{
     String type=p.endsWith(".html")?"text/html":p.endsWith(".css")?"text/css":p.endsWith(".js")?"application/javascript":p.endsWith(".mp3")?"audio/mpeg":p.endsWith(".wav")?"audio/wav":"application/octet-stream";
     return new WebResourceResponse(type,"UTF-8",getAssets().open(p.substring(1)));
    }catch(IOException ex){return empty();}
   }
   @Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return navigate(r.getUrl());}
   @Override public boolean shouldOverrideUrlLoading(WebView v,String url){return navigate(Uri.parse(url));}
  });
  web.loadUrl(BASE+"index.html"); immersive();
 }
 private WebResourceResponse empty(){return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));}
 private boolean navigate(Uri u){
  if("https".equals(u.getScheme())&&"appassets.androidplatform.net".equals(u.getHost())&&Arrays.asList("/index.html","/library.html").contains(u.getPath()))return false;
  external(u.toString());return true;
 }
 private void immersive(){getWindow().getDecorView().setSystemUiVisibility(5894);}
 @Override public void onWindowFocusChanged(boolean focus){super.onWindowFocusChanged(focus);if(focus)immersive();}
 @Override protected void onResume(){super.onResume();foreground=true;if(web!=null){web.onResume();web.evaluateJavascript("document.querySelectorAll('audio').forEach(a=>{if(a.id==='bgm')a.play().catch(()=>{});})",null);}}
 @Override protected void onPause(){foreground=false;if(web!=null){web.evaluateJavascript("document.querySelectorAll('audio,video').forEach(a=>a.pause())",null);web.onPause();}super.onPause();}
 @Override protected void onDestroy(){if(web!=null){web.removeJavascriptInterface("Android");web.destroy();}super.onDestroy();}
 @Override public void onBackPressed(){
  if(web.getUrl()!=null&&!web.getUrl().endsWith("/index.html"))web.loadUrl(BASE+"index.html");
  else new AlertDialog.Builder(this).setMessage("PRIVATE SUITEを終了しますか？").setPositiveButton("終了",(d,w)->finish()).setNegativeButton("戻る",null).show();
 }
 @Override public boolean dispatchKeyEvent(KeyEvent e){
  String key=null;
  switch(e.getKeyCode()){
   case KeyEvent.KEYCODE_DPAD_LEFT:key="ArrowLeft";break;
   case KeyEvent.KEYCODE_DPAD_RIGHT:key="ArrowRight";break;
   case KeyEvent.KEYCODE_DPAD_UP:key="ArrowUp";break;
   case KeyEvent.KEYCODE_DPAD_DOWN:key="ArrowDown";break;
   case KeyEvent.KEYCODE_DPAD_CENTER:case KeyEvent.KEYCODE_ENTER:key="Enter";break;
  }
  if(key!=null&&hasWindowFocus()){if(e.getAction()==KeyEvent.ACTION_DOWN)web.evaluateJavascript("window.nativeKey&&window.nativeKey("+JSONObject.quote(key)+")",null);return true;}
  return super.dispatchKeyEvent(e);
 }
 private void toast(String text){Toast.makeText(this,text,Toast.LENGTH_LONG).show();}
 private void external(String value){
  Uri u=Uri.parse(value);if(!Arrays.asList("https","http").contains(u.getScheme())){toast("このリンク形式には対応していません");return;}
  String h=u.getHost()==null?"":u.getHost().toLowerCase(Locale.ROOT);
  String[] packages=(h.equals("www.youtube.com")||h.equals("youtube.com")||h.equals("m.youtube.com"))?new String[]{"com.google.android.youtube.tv","com.google.android.youtube"}:
    (h.equals("tver.jp")||h.equals("www.tver.jp"))?new String[]{"jp.hamitv.hamiand1","jp.hamitv.hamiand1.tv"}:
    (h.equals("abema.tv")||h.equals("www.abema.tv"))?new String[]{"tv.abema","tv.abema.tv","com.abema.tv"}:
    (h.equals("netflix.com")||h.equals("www.netflix.com"))?new String[]{"com.netflix.ninja","com.netflix.mediaclient"}:
    h.equals("www.amazon.co.jp")?new String[]{"com.amazon.amazonvideo.livingroom","com.amazon.avod.thirdpartyclient"}:new String[0];
  // 動画サービスは、アプリが無ければ互換性の低いWebViewに落とさず説明する。
  boolean appOnly=packages.length>0;
  for(String p:packages){
   Intent i=getPackageManager().getLeanbackLaunchIntentForPackage(p);
   if(i==null)i=getPackageManager().getLaunchIntentForPackage(p);
   if(i!=null){try{startActivity(i);return;}catch(ActivityNotFoundException ignored){}}
  }
  if(appOnly){toast("対応するTVアプリがありません。アプリの対応OS・インストール状態を確認してください");return;}
  try{startActivity(new Intent(Intent.ACTION_VIEW,u).addCategory(Intent.CATEGORY_BROWSABLE));}
  catch(ActivityNotFoundException ex){toast("対応アプリまたはブラウザをインストールしてください");}
 }
 private void play(String value){
  Uri u=Uri.parse(value);if(!Arrays.asList("http","https","content").contains(u.getScheme())){toast("再生できない形式です");return;}
  Intent i=new Intent(Intent.ACTION_VIEW).setDataAndType(u,"video/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
  i.setClipData(ClipData.newRawUri("media",u));
  try{startActivity(new Intent(i).setPackage("org.videolan.vlc"));}
  catch(ActivityNotFoundException ex){try{startActivity(i);}catch(ActivityNotFoundException e){toast("VLCなどの動画再生アプリをインストールしてください");}}
 }
 private void pick(int code){
  Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType(code==VIDEO?"video/*":"*/*")
    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
  try{startActivityForResult(i,code);}catch(ActivityNotFoundException ex){toast("この端末にはファイル選択機能がありません。URL再生を利用できます");}
 }
 @Override protected void onActivityResult(int request,int result,Intent data){
  super.onActivityResult(request,result,data);if(result!=RESULT_OK||data==null||data.getData()==null)return;
  Uri uri=data.getData();
  if(request==VIDEO){
   try{getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(SecurityException ignored){}
   play(uri.toString());return;
  }
  if(request==PLAYLIST)new Thread(()->{
   try(InputStream in=getContentResolver().openInputStream(uri)){
    ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] buf=new byte[8192];int n;
    while((n=in.read(buf))!=-1){if(bytes.size()+n>2*1024*1024)throw new IOException("too large");bytes.write(buf,0,n);}
    JSONArray arr=parseM3u(new String(bytes.toByteArray(),StandardCharsets.UTF_8));
    if(arr.length()==0)throw new IOException("empty");
    getPreferences(0).edit().putString("channels",arr.toString()).apply();
    runOnUiThread(()->{web.reload();toast(arr.length()+"件を読み込みました");});
   }catch(Exception ex){runOnUiThread(()->toast("読み込めませんでした。2MB以内のUTF-8 M3U（HTTP/HTTPS）を選択してください"));}
  }).start();
 }
 static JSONArray parseM3u(String text)throws JSONException{
  JSONArray arr=new JSONArray();String name="";
  for(String line:text.replace("\uFEFF","").split("\\r?\\n")){
   line=line.trim();
   if(line.startsWith("#EXTINF:")){int comma=line.indexOf(',');name=comma>=0?line.substring(comma+1):"";}
   else if(line.startsWith("https://")||line.startsWith("http://")){arr.put(new JSONObject().put("name",name.isEmpty()?line:name).put("source",line));name="";}
  }return arr;
 }
 private void urlDialog(){
  EditText input=new EditText(this);input.setSingleLine(true);input.setHint("https://…");input.setInputType(17);
  new AlertDialog.Builder(this).setTitle("動画・配信URL").setView(input).setPositiveButton("再生",(d,w)->play(input.getText().toString().trim())).setNegativeButton("キャンセル",null).show();
 }
 public class Bridge{
  @JavascriptInterface public void openExternal(String u){runOnUiThread(()->external(u));}
  @JavascriptInterface public void playMedia(String u){runOnUiThread(()->play(u));}
  @JavascriptInterface public void pickVideo(){runOnUiThread(()->pick(VIDEO));}
  @JavascriptInterface public void pickPlaylist(){runOnUiThread(()->pick(PLAYLIST));}
  @JavascriptInterface public void enterUrl(){runOnUiThread(()->urlDialog());}
  @JavascriptInterface public String channels(){return getPreferences(0).getString("channels","");}
  @JavascriptInterface public void resetChannels(){getPreferences(0).edit().remove("channels").apply();runOnUiThread(()->web.reload());}
 }
}
