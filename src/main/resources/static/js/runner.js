/* runner.js — LearnCode Adventure Spring Boot */
const canvas = document.getElementById('runner-canvas');
const ctx    = canvas.getContext('2d');
const W = canvas.width, H = canvas.height, GROUND = 155;
let currentDiff = typeof currentDiffInit !== 'undefined' ? currentDiffInit : 'easy';
let state = {};

function resetState() {
  state = {
    px:90, py:GROUND, velY:0, onGround:true,
    obstacles:[], stars:[], doors:[],
    bgX:0, frame:0, tick:0,
    alive:true, invincible:0,
    score:0, quizOpen:false, doorPassed:false,
    animId:null,
    speed: currentDiff==='hard'?6.5:currentDiff==='medium'?5:3.5
  };
}

function loop() {
  state.animId = requestAnimationFrame(loop);
  state.tick++;
  if (!state.quizOpen) update();
  draw();
}

function update() {
  const spd = state.speed;
  state.frame = (state.frame + 1) % 20;
  state.invincible = Math.max(0, state.invincible - 1/60);

  // Gravité
  state.velY += 26/60;
  state.py   += state.velY;
  if (state.py >= GROUND) { state.py = GROUND; state.velY = 0; state.onGround = true; }

  state.bgX = (state.bgX - spd * 0.4 + W) % W;

  // Spawns
  const iv = currentDiff==='hard'?55:currentDiff==='medium'?80:110;
  if (state.tick % iv === 0)
    state.obstacles.push({ x: W+20, w:22, h:30+Math.random()*22 });
  if (state.tick % 65 === 0)
    state.stars.push({ x:W+10, y:GROUND-40-Math.random()*45, col:false });
  if (state.tick % 280 === 0 && !state.doorPassed)
    state.doors.push({ x: W+20 });

  // Move
  state.obstacles.forEach(o => o.x -= spd);
  state.stars.forEach(s    => s.x -= spd);
  state.doors.forEach(d    => d.x -= spd);

  // Collisions obstacles
  if (state.invincible <= 0) {
    for (const o of state.obstacles) {
      if (state.px+28 > o.x && state.px < o.x+o.w && state.py+42 > GROUND-o.h) {
        state.invincible = 1.5;
        state.obstacles  = [];
        postJSON('/api/runner', { action:'vie' }).then(d => {
          updateHUD(d.score, d.niveau, d.vies);
          if (d.gameOver) showGameOver();
        });
        playWrong();
        break;
      }
    }
  }

  // Étoiles
  state.stars.forEach(s => {
    if (!s.col && Math.abs(state.px+15-s.x) < 24 && Math.abs(state.py+20-s.y) < 24) {
      s.col = true; state.score++;
      postJSON('/api/runner', { action:'etoile' }).then(d => updateHUD(d.score));
      playCorrect();
      floatScore(2,
        canvas.getBoundingClientRect().left + state.px,
        canvas.getBoundingClientRect().top  + state.py - 30);
    }
  });

  // Porte → quiz
  state.doors.forEach(d => {
    if (!state.doorPassed && state.px+30 > d.x && state.px < d.x+50) {
      state.doorPassed = true;
      state.doors = [];
      openQuiz();
    }
  });

  // Nettoyage
  state.obstacles = state.obstacles.filter(o => o.x > -50);
  state.stars     = state.stars.filter(s     => s.x > -20);
}

// ── Dessin ────────────────────────────────────────────────────────
function draw() {
  ctx.clearRect(0, 0, W, H);

  // Ciel
  const sky = ctx.createLinearGradient(0,0,0,H);
  sky.addColorStop(0,'#0d1b4b'); sky.addColorStop(1,'#1a2a6c');
  ctx.fillStyle = sky; ctx.fillRect(0,0,W,H);

  // Étoiles fond
  ctx.fillStyle = 'rgba(255,255,255,.6)';
  for (let i=0; i<24; i++) {
    ctx.beginPath();
    ctx.arc(((i*131+state.bgX*.3)%W+W)%W, (i*43)%(H-80)+5, 1, 0, Math.PI*2);
    ctx.fill();
  }

  // Sol
  const gy = GROUND + 40;
  ctx.fillStyle = '#1a3a2a'; ctx.fillRect(0, gy, W, H-gy);
  ctx.fillStyle = '#2e7d32'; ctx.fillRect(0, gy-4, W, 9);
  ctx.fillStyle = '#43a047';
  for (let i=0; i<W; i+=40) {
    ctx.beginPath();
    ctx.arc(((i+state.bgX*2)%W+W)%W, gy-3, 5, Math.PI, 0);
    ctx.fill();
  }

  // Étoiles collectibles
  state.stars.forEach(s => { if (!s.col) drawStar(s.x, s.y, 9); });

  // Portes
  state.doors.forEach(d => drawDoor(d.x, gy));

  // Monstres
  state.obstacles.forEach(o => drawMonster(o.x, gy-o.h, o.w, o.h));

  // Personnage
  const flicker = state.invincible > 0 && Math.floor(state.tick/3)%2===0;
  if (!flicker) drawPlayer();

  // Score
  ctx.fillStyle = 'rgba(255,255,255,.9)';
  ctx.font = 'bold 13px Arial';
  ctx.fillText('Etoiles: '+state.score, 10, 20);
  ctx.fillText(currentDiff, W-70, 20);
}

function drawStar(x, y, r) {
  ctx.fillStyle = '#ffd700';
  ctx.beginPath();
  for (let i=0; i<10; i++) {
    const a  = Math.PI/5*i - Math.PI/2;
    const rr = i%2===0 ? r : r*0.45;
    i===0 ? ctx.moveTo(x+rr*Math.cos(a), y+rr*Math.sin(a))
           : ctx.lineTo(x+rr*Math.cos(a), y+rr*Math.sin(a));
  }
  ctx.closePath(); ctx.fill();
}

function drawDoor(dx, gy) {
  ctx.fillStyle='#8B4513'; ctx.fillRect(dx, gy-65, 45, 65);
  ctx.fillStyle='#A0522D'; ctx.fillRect(dx+2, gy-63, 41, 61);
  ctx.fillStyle='#FFD700';
  ctx.beginPath(); ctx.arc(dx+35, gy-32, 4, 0, Math.PI*2); ctx.fill();
  ctx.strokeStyle='rgba(108,99,255,.9)'; ctx.lineWidth=3;
  ctx.strokeRect(dx-2, gy-67, 49, 69);
  ctx.fillStyle='#a29bfe'; ctx.font='bold 11px Arial';
  ctx.fillText('QUIZ', dx+6, gy-71);
}

function drawMonster(ox, oy, ow, oh) {
  ctx.fillStyle='#e53935';
  ctx.beginPath(); ctx.roundRect(ox, oy, ow, oh, 5); ctx.fill();
  ctx.fillStyle='white';
  ctx.beginPath(); ctx.arc(ox+6,  oy+8, 4, 0, Math.PI*2); ctx.fill();
  ctx.beginPath(); ctx.arc(ox+16, oy+8, 4, 0, Math.PI*2); ctx.fill();
  ctx.fillStyle='#212121';
  ctx.beginPath(); ctx.arc(ox+7,  oy+9, 2, 0, Math.PI*2); ctx.fill();
  ctx.beginPath(); ctx.arc(ox+17, oy+9, 2, 0, Math.PI*2); ctx.fill();
  ctx.strokeStyle='#212121'; ctx.lineWidth=2;
  ctx.beginPath(); ctx.arc(ox+11, oy+17, 5, 0, Math.PI); ctx.stroke();
}

function drawPlayer() {
  const run  = Math.floor(state.frame/5) % 2;
  const px   = state.px, py = state.py;
  const skin = '#f4a261';
  const bodyC= '#1976d2', legC='#0d47a1', hairC='#2c1a0e';

  // Jambes
  ctx.fillStyle = legC;
  if (run===0) { ctx.fillRect(px+7,py+30,9,13); ctx.fillRect(px+20,py+30,9,9); }
  else         { ctx.fillRect(px+7,py+30,9,9);  ctx.fillRect(px+20,py+30,9,13); }

  // Chaussures
  ctx.fillStyle = '#212121';
  ctx.beginPath(); ctx.ellipse(px+12, py+44, 7, 4, 0, 0, Math.PI*2); ctx.fill();
  ctx.beginPath(); ctx.ellipse(px+25, run===0?py+40:py+44, 7, 4, 0, 0, Math.PI*2); ctx.fill();

  // Corps
  ctx.fillStyle = bodyC;
  ctx.beginPath(); ctx.roundRect(px+5, py+14, 26, 18, 5); ctx.fill();

  // Bras
  ctx.fillStyle = skin;
  if (run===0) { ctx.fillRect(px+1,py+16,6,12); ctx.fillRect(px+29,py+20,6,12); }
  else         { ctx.fillRect(px+1,py+20,6,12); ctx.fillRect(px+29,py+16,6,12); }

  // Tête
  ctx.fillStyle = skin;
  ctx.beginPath(); ctx.arc(px+18, py+9, 13, 0, Math.PI*2); ctx.fill();

  // Cheveux + casquette
  ctx.fillStyle = hairC;
  ctx.beginPath(); ctx.arc(px+18, py+2, 12, Math.PI, 0); ctx.fill();
  ctx.fillStyle = '#e53935'; ctx.fillRect(px+6, py-2, 24, 8);

  // Yeux
  ctx.fillStyle = 'white';
  ctx.beginPath(); ctx.ellipse(px+13, py+9, 4, 4.5, 0, 0, Math.PI*2); ctx.fill();
  ctx.beginPath(); ctx.ellipse(px+23, py+9, 4, 4.5, 0, 0, Math.PI*2); ctx.fill();
  ctx.fillStyle = '#1a0d06';
  ctx.beginPath(); ctx.arc(px+14, py+10, 2.2, 0, Math.PI*2); ctx.fill();
  ctx.beginPath(); ctx.arc(px+24, py+10, 2.2, 0, Math.PI*2); ctx.fill();
}

// ── Quiz ──────────────────────────────────────────────────────────
let quizTimer=null, currentQ=null;

async function openQuiz() {
  state.quizOpen = true;
  const res  = await fetch('/api/question?diff=' + currentDiff);
  currentQ   = await res.json();

  const modal = document.getElementById('quiz-modal');
  document.getElementById('quiz-question').textContent = currentQ.question;

  const opts = document.getElementById('quiz-options');
  opts.innerHTML = '';
  currentQ.options.forEach((o, i) => {
    const btn = document.createElement('button');
    btn.className   = 'quiz-opt';
    btn.textContent = ['A','B','C','D'][i] + ') ' + o;
    btn.onclick     = () => answerQuiz(i);
    opts.appendChild(btn);
  });

  document.getElementById('quiz-result').style.display = 'none';
  modal.style.display = 'flex';

  // Timer 30s
  let left = 30;
  const fill = document.getElementById('quiz-timer-fill');
  fill.style.transition = 'none'; fill.style.width = '100%';
  setTimeout(() => { fill.style.transition = 'width 30s linear'; fill.style.width = '0%'; }, 30);
  clearInterval(quizTimer);
  quizTimer = setInterval(() => {
    left--;
    if (left <= 0) {
      clearInterval(quizTimer);
      closeQuizModal();
      playWrong();
      postJSON('/api/runner', { action:'quiz', questionId:currentQ.id, reponse:-1 })
        .then(d => {
          updateHUD(d.score, d.niveau, d.vies);
          handleBadges(d.badges);
          state.doorPassed = false;
          if (d.gameOver) showGameOver();
        });
    }
  }, 1000);
}

async function answerQuiz(idx) {
  clearInterval(quizTimer);
  document.querySelectorAll('.quiz-opt').forEach(b => b.disabled = true);

  const data = await postJSON('/api/runner', {
    action: 'quiz', questionId: currentQ.id, reponse: idx
  });

  document.querySelectorAll('.quiz-opt').forEach((b, i) => {
    if (data.correct && i === idx) b.classList.add('ok');
    if (!data.correct && i === idx) b.classList.add('wrong');
  });

  const res = document.getElementById('quiz-result');
  res.textContent  = data.correct ? 'Correct ! +20 points !' : 'Mauvaise reponse... -1 vie !';
  res.className    = 'quiz-result ' + (data.correct ? 'ok' : 'err');
  res.style.display= 'block';

  updateHUD(data.score, data.niveau, data.vies);
  handleBadges(data.badges);
  data.correct ? playCorrect() : playWrong();
  if (data.correct) floatScore(20, canvas.getBoundingClientRect().left + W/2, 200);

  setTimeout(() => {
    closeQuizModal();
    state.doorPassed = false;
    if (data.gameOver) showGameOver();
  }, 2000);
}

function closeQuizModal() {
  document.getElementById('quiz-modal').style.display = 'none';
  state.quizOpen = false;
  clearInterval(quizTimer);
}

function showGameOver() {
  if (state.animId) cancelAnimationFrame(state.animId);
  document.getElementById('modal-gameover').style.display = 'flex';
}

// ── Contrôles ────────────────────────────────────────────────────
function runnerJump() {
  if (state.onGround && !state.quizOpen) {
    state.velY = -11; state.onGround = false; playClick();
  }
}

function setDiff(d) {
  currentDiff = d;
  document.querySelectorAll('.diff-btn').forEach(b => b.classList.remove('active'));
  event.target.classList.add('active');
  if (state.animId) cancelAnimationFrame(state.animId);
  resetState(); loop();
}

document.addEventListener('keydown', e => {
  if ((e.code==='Space' || e.key===' ') && !state.quizOpen) {
    e.preventDefault(); runnerJump();
  }
});
canvas.addEventListener('click', () => { if (!state.quizOpen) runnerJump(); });

// ── Démarrage ────────────────────────────────────────────────────
resetState();
loop();
