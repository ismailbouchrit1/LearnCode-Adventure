/* main.js */
let _ctx=null;
function getAudio(){if(!_ctx)_ctx=new(window.AudioContext||window.webkitAudioContext)();return _ctx;}
function playSound(type){
  try{
    const ctx=getAudio(),osc=ctx.createOscillator(),g=ctx.createGain();
    osc.connect(g);g.connect(ctx.destination);const t=ctx.currentTime;
    if(type==='click'){osc.frequency.value=880;g.gain.setValueAtTime(.12,t);g.gain.exponentialRampToValueAtTime(.001,t+.1);}
    else if(type==='correct'){osc.frequency.setValueAtTime(523,t);osc.frequency.setValueAtTime(659,t+.1);osc.frequency.setValueAtTime(784,t+.2);g.gain.setValueAtTime(.18,t);g.gain.exponentialRampToValueAtTime(.001,t+.4);}
    else if(type==='wrong'){osc.frequency.setValueAtTime(300,t);osc.frequency.setValueAtTime(220,t+.15);g.gain.setValueAtTime(.15,t);g.gain.exponentialRampToValueAtTime(.001,t+.3);}
    osc.start(t);osc.stop(t+.5);
  }catch(e){}
}
function playClick(){playSound('click');}
function playCorrect(){playSound('correct');}
function playWrong(){playSound('wrong');}

function floatScore(pts,x,y){
  const el=document.createElement('div');
  el.className='float-score';
  el.textContent=(pts>0?'+':'')+pts+' pts';
  el.style.color=pts>0?'#ffd700':'#ff6584';
  el.style.left=(x||window.innerWidth/2)+'px';
  el.style.top=(y||200)+'px';
  document.body.appendChild(el);
  setTimeout(()=>el.remove(),1000);
}

function spawnConfetti(count){
  const cols=['#6c63ff','#ff6584','#43e97b','#f7971e','#ffd700','#00cec9'];
  for(let i=0;i<(count||60);i++){
    setTimeout(()=>{
      const c=document.createElement('div');
      c.className='confetti-piece';
      c.style.left=Math.random()*100+'vw';
      c.style.background=cols[Math.floor(Math.random()*cols.length)];
      c.style.animationDuration=(Math.random()*2+1.4)+'s';
      const sz=Math.random()*8+4;
      c.style.width=c.style.height=sz+'px';
      c.style.borderRadius=Math.random()>.5?'50%':'2px';
      document.body.appendChild(c);
      setTimeout(()=>c.remove(),4000);
    },i*35);
  }
}

function updateHUD(score,niveau,vies){
  const sc=document.getElementById('hud-score');
  const nv=document.getElementById('hud-niveau');
  if(sc&&score!==undefined)sc.textContent=score;
  if(nv&&niveau!==undefined)nv.textContent=niveau;
  if(vies!==undefined){
    document.querySelectorAll('.heart-icon').forEach((h,i)=>{
      h.classList.toggle('full',i<vies);
      h.classList.toggle('empty',i>=vies);
    });
  }
}

function showBadgeToast(name){
  const t=document.createElement('div');
  t.style.cssText='position:fixed;bottom:30px;right:24px;z-index:500;background:#12122a;border:2px solid #ffd700;border-radius:16px;padding:14px 20px;font-family:"Fredoka One",cursive;color:#ffd700;font-size:1rem;box-shadow:0 8px 30px rgba(255,215,0,.3);animation:slideUp .4s ease;';
  t.textContent='Badge debloque : '+name+' !';
  document.body.appendChild(t);
  setTimeout(()=>t.remove(),3500);
}

function handleBadges(badges){
  if(badges&&badges.length)badges.forEach(b=>setTimeout(()=>showBadgeToast(b),500));
}

async function postJSON(url,data){
  const params=new URLSearchParams(data);
  const res=await fetch(url,{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:params.toString()});
  return res.json();
}
