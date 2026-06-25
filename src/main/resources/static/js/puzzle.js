/* puzzle.js */
const KW=['print','input','if','else','elif','for','while','def','return','import','from','class','in','range','int','str','float','len','True','False','None','and','or','not'];
function colorize(code){
  let out='',i=0;
  const esc=s=>s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
  while(i<code.length){
    if(code[i]==='#'){out+=`<span class="cmt">${esc(code.slice(i))}</span>`;break;}
    if(code[i]==='"'){let j=i+1;while(j<code.length&&code[j]!=='"')j++;out+=`<span class="str">${esc(code.slice(i,j+1))}</span>`;i=j+1;continue;}
    if(code[i]==="'"){let j=i+1;while(j<code.length&&code[j]!=="'")j++;out+=`<span class="str">${esc(code.slice(i,j+1))}</span>`;i=j+1;continue;}
    if(/[a-zA-Z_]/.test(code[i])){let j=i;while(j<code.length&&/\w/.test(code[j]))j++;const w=code.slice(i,j);if(KW.includes(w))out+=`<span class="kw">${w}</span>`;else if(code[j]==='(')out+=`<span class="fn">${w}</span>`;else out+=esc(w);i=j;continue;}
    if(/\d/.test(code[i])){let j=i;while(j<code.length&&/[\d.]/.test(code[j]))j++;out+=`<span class="num">${code.slice(i,j)}</span>`;i=j;continue;}
    out+=esc(code[i]);i++;
  }
  return out;
}
document.querySelectorAll('.code-content').forEach(el=>{el.innerHTML=colorize(el.textContent);});

let dragSrc=null;
document.querySelectorAll('.code-line').forEach(line=>{
  line.addEventListener('dragstart',function(e){dragSrc=this;this.classList.add('dragging');e.dataTransfer.effectAllowed='move';playClick();});
  line.addEventListener('dragend',function(){this.classList.remove('dragging');document.querySelectorAll('.code-line').forEach(l=>l.classList.remove('drag-over'));renumber();});
  line.addEventListener('dragover',function(e){e.preventDefault();document.querySelectorAll('.code-line').forEach(l=>l.classList.remove('drag-over'));if(this!==dragSrc)this.classList.add('drag-over');});
  line.addEventListener('drop',function(e){
    e.stopPropagation();
    if(dragSrc&&dragSrc!==this){
      const area=document.getElementById('puzzle-area');
      const ch=[...area.children];
      const si=ch.indexOf(dragSrc),ti=ch.indexOf(this);
      if(si<ti)area.insertBefore(dragSrc,this.nextSibling);else area.insertBefore(dragSrc,this);
    }
    this.classList.remove('drag-over');
  });
  // Touch
  let ty=0;
  line.addEventListener('touchstart',e=>{dragSrc=line;ty=e.touches[0].clientY;playClick();},{passive:true});
  line.addEventListener('touchmove',e=>{
    e.preventDefault();
    const y=e.touches[0].clientY;
    const area=document.getElementById('puzzle-area');
    let tgt=null;
    [...area.querySelectorAll('.code-line')].forEach(l=>{const r=l.getBoundingClientRect();if(y>r.top&&y<r.bottom&&l!==dragSrc)tgt=l;});
    area.querySelectorAll('.code-line').forEach(l=>l.classList.remove('drag-over'));
    if(tgt)tgt.classList.add('drag-over');
  },{passive:false});
  line.addEventListener('touchend',e=>{
    const area=document.getElementById('puzzle-area');
    const tgt=area.querySelector('.code-line.drag-over');
    if(tgt&&dragSrc&&tgt!==dragSrc){const ch=[...area.children];const si=ch.indexOf(dragSrc),ti=ch.indexOf(tgt);if(si<ti)area.insertBefore(dragSrc,tgt.nextSibling);else area.insertBefore(dragSrc,tgt);}
    area.querySelectorAll('.code-line').forEach(l=>l.classList.remove('drag-over'));
    renumber();dragSrc=null;
  });
});

function renumber(){document.querySelectorAll('.code-line .line-num').forEach((n,i)=>n.textContent=i+1);}

function verifierPuzzle(){
  const area=document.getElementById('puzzle-area');
  const order=[...area.querySelectorAll('.code-line')].map(l=>parseInt(l.dataset.lineIdx));
  document.getElementById('reponse-input').value=order.join(',');
  playClick();
  document.getElementById('puzzle-form').submit();
}
