/* memory.js — Spring Boot version */
// PAIRES_DATA et NB_PAIRES sont injectés par Thymeleaf dans game_memory.html

let flipped=[],matched=0,errors=0,locked=false,startTime=Date.now(),timerInt=null;

function buildCards(){
  const cards=[];
  PAIRES_DATA.forEach(p=>{
    cards.push({pairId:p.id,type:'keyword',text:p.keyword});
    cards.push({pairId:p.id,type:'meaning',text:p.meaning});
  });
  for(let i=cards.length-1;i>0;i--){const j=Math.floor(Math.random()*(i+1));[cards[i],cards[j]]=[cards[j],cards[i]];}
  return cards;
}

function renderCards(cards){
  const grid=document.getElementById('memory-grid');
  grid.innerHTML='';
  cards.forEach((card,idx)=>{
    const div=document.createElement('div');
    div.className='mem-card';div.dataset.pairId=card.pairId;div.dataset.type=card.type;div.dataset.idx=idx;
    div.innerHTML=`<div class="mem-front"><span class="q-mark">?</span></div>
      <div class="mem-back ${card.type==='meaning'?'meaning-card':''}">
        <span class="card-text">${esc(card.text)}</span>
        <span class="card-type">${card.type==='keyword'?'Mot-cle':'Definition'}</span>
      </div>`;
    div.addEventListener('click',()=>flipCard(div));
    grid.appendChild(div);
  });
}

function esc(s){return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');}

function flipCard(card){
  if(locked)return;
  if(card.classList.contains('flipped')||card.classList.contains('matched'))return;
  if(flipped.length>=2)return;
  card.classList.add('flipped');flipped.push(card);playClick();
  if(flipped.length===2){
    locked=true;
    const[a,b]=flipped;
    if(a.dataset.pairId===b.dataset.pairId&&a.dataset.type!==b.dataset.type){
      setTimeout(()=>{
        a.classList.add('matched');b.classList.add('matched');
        matched++;updateStats();playCorrect();spawnConfetti(12);
        postJSON('/api/memory',{action:'paire'}).then(d=>{updateHUD(d.score,d.niveau,d.vies);handleBadges(d.badges);});
        flipped=[];locked=false;
        floatScore(5,a.getBoundingClientRect().left,a.getBoundingClientRect().top);
        if(matched===NB_PAIRES)finishGame();
      },400);
    }else{
      setTimeout(()=>{a.classList.remove('flipped');b.classList.remove('flipped');errors++;updateStats();playWrong();flipped=[];locked=false;},900);
    }
  }
}

function updateStats(){
  document.getElementById('pairs-count').textContent=matched;
  document.getElementById('errors-count').textContent=errors;
}

function finishGame(){
  clearInterval(timerInt);
  const elapsed=Math.floor((Date.now()-startTime)/1000);
  const bonus=Math.max(0,200-elapsed*2);
  postJSON('/api/memory',{action:'fin',temps:elapsed}).then(d=>{updateHUD(d.score,d.niveau,d.vies);handleBadges(d.badges);});
  const ov=document.getElementById('victory-overlay');
  document.getElementById('victory-text').textContent=`Toutes les ${NB_PAIRES} paires ! Temps : ${fmt(elapsed)} | Erreurs : ${errors} | Bonus : +${bonus} pts`;
  ov.style.display='flex';spawnConfetti(80);playCorrect();
}

function fmt(s){return String(Math.floor(s/60)).padStart(2,'0')+':'+String(s%60).padStart(2,'0');}

timerInt=setInterval(()=>{
  document.getElementById('timer-display').textContent=fmt(Math.floor((Date.now()-startTime)/1000));
},1000);

renderCards(buildCards());
document.getElementById('pairs-total').textContent=NB_PAIRES;
