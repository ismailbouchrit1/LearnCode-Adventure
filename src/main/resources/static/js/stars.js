/* stars.js */
(function(){
  const sf=document.getElementById('starfield');
  if(!sf)return;
  for(let i=0;i<110;i++){
    const el=document.createElement('div');
    el.className='star-dot';
    const s=Math.random()*2.8+0.6;
    el.style.cssText=`width:${s}px;height:${s}px;left:${Math.random()*100}%;top:${Math.random()*100}%;animation-duration:${(Math.random()*4+2).toFixed(1)}s;animation-delay:${(Math.random()*6).toFixed(1)}s`;
    sf.appendChild(el);
  }
})();
