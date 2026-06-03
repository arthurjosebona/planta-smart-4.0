const CORES_BLOCOS = [
    { value: 1, descricao: 'Preto' },
    { value: 2, descricao: 'Vermelho' },
    { value: 3, descricao: 'Azul' }
];

const CORES_LAMINAS = [
    { value: 1, descricao: 'Vermelho' },
    { value: 2, descricao: 'Azul' },
    { value: 3, descricao: 'Amarelo' },
    { value: 4, descricao: 'Verde' },
    { value: 5, descricao: 'Preto' },
    { value: 6, descricao: 'Branco' }
];

const PADROES_LAMINA = [
    { value: 0, descricao: 'Nenhum' },
    { value: 1, descricao: 'Casa' },
    { value: 2, descricao: 'Navio' },
    { value: 3, descricao: 'Estrela' }
];

const POSICOES_LAMINA = [
    { value: 1, descricao: 'Esquerda' },
    { value: 2, descricao: 'Frente' },
    { value: 3, descricao: 'Direita' }
];

// ─── helpers ──────────────────────────────────────────────────────────────────
function opt(arr, selectedVal, placeholder) {
  let html = placeholder ? `<option value="">${placeholder}</option>` : '';
  arr.forEach(item => {
    const sel = item.value == selectedVal ? 'selected' : '';
    html += `<option value="${item.value}" ${sel}>${item.descricao}</option>`;
  });
  return html;
}

function getVal(id) { return document.getElementById(id)?.value || ''; }

// ─── estado ───────────────────────────────────────────────────────────────────
let numBlocos = 0;

// ─── tipo muda → reconstrói blocos ────────────────────────────────────────────
function onTipoChange() {
  const tipo = parseInt(getVal('tipoPedido')) || 0;
  numBlocos = tipo; // 1=simples, 2=duplo, 3=triplo
  renderBlocos();
  validarForm();
}

// ─── render de todos os blocos ────────────────────────────────────────────────
function renderBlocos() {
  const container = document.getElementById('blocos-container');
  const atual = container.querySelectorAll('.bloco-section').length;

  if (numBlocos > atual) {
    for (let i = atual; i < numBlocos; i++) addBlocoDOM(i);
  } else {
    const secoes = container.querySelectorAll('.bloco-section');
    for (let i = numBlocos; i < atual; i++) secoes[i].remove();
  }
}

function addBlocoDOM(idx) {
  const container = document.getElementById('blocos-container');
  const div = document.createElement('div');
  div.className = 'bloco-section';
  div.id = `bloco-${idx}`;
  div.innerHTML = `
    <div class="bloco-title">Bloco ${idx + 1} (Andar ${idx + 1})</div>

    <div>
      <label>Cor do Bloco *</label>
      <select id="bloco-${idx}-cor" onchange="validarForm()">
        ${opt(CORES_BLOCOS, '', '-- selecione --')}
      </select>
    </div>
    <br>

    <div>
      <strong style="font-size:13px;">Lâminas (máx. 3)</strong>
      <div id="laminas-${idx}"></div>
      <button class="add-btn" onclick="addLamina(${idx})">+ Adicionar Lâmina</button>
    </div>
  `;
  container.appendChild(div);
}

// ─── lâminas ──────────────────────────────────────────────────────────────────
function addLamina(blocoIdx) {
  const container = document.getElementById(`laminas-${blocoIdx}`);
  const atual = container.querySelectorAll('.lamina-row').length;
  if (atual >= 3) { alert('Máximo de 3 lâminas por bloco.'); return; }

  const lIdx = atual;
  const div = document.createElement('div');
  div.className = 'lamina-row';
  div.id = `lamina-${blocoIdx}-${lIdx}`;
  div.innerHTML = `
    <div class="lamina-title">
      Lâmina ${lIdx + 1}
      <button class="rem-btn" onclick="removeLamina(${blocoIdx}, ${lIdx})">Remover</button>
    </div>
    <label>Cor *</label>
    <select id="lamina-${blocoIdx}-${lIdx}-cor" onchange="validarForm()">
      ${opt(CORES_LAMINAS, '', '-- selecione --')}
    </select>
    &nbsp;
    <label>Padrão</label>
    <select id="lamina-${blocoIdx}-${lIdx}-padrao">
      ${opt(PADROES_LAMINA, 0)}
    </select>
    &nbsp;
    <label>Posição *</label>
    <select id="lamina-${blocoIdx}-${lIdx}-posicao" onchange="validarForm()">
      ${opt(POSICOES_LAMINA, '', '-- selecione --')}
    </select>
  `;
  container.appendChild(div);
  validarForm();
}

function removeLamina(blocoIdx, lIdx) {
  document.getElementById(`lamina-${blocoIdx}-${lIdx}`)?.remove();
  // reindexar labels visuais
  document.querySelectorAll(`#laminas-${blocoIdx} .lamina-row`).forEach((r, i) => {
    r.querySelector('.lamina-title').childNodes[0].textContent = `Lâmina ${i + 1} `;
  });
  validarForm();
}

// ─── validação ────────────────────────────────────────────────────────────────
function validarForm() {
  let ok = true;

  if (!getVal('ordemProducao')) ok = false;
  if (!getVal('tipoPedido'))    ok = false;
  if (!getVal('corTampa'))      ok = false;

  for (let i = 0; i < numBlocos && ok; i++) {
    if (!getVal(`bloco-${i}-cor`))     { ok = false; break; }

    document.querySelectorAll(`#laminas-${i} .lamina-row`).forEach(row => {
      const [, b, l] = row.id.split('-');
      if (!getVal(`lamina-${b}-${l}-cor`))     ok = false;
      if (!getVal(`lamina-${b}-${l}-posicao`)) ok = false;
    });
  }

  document.getElementById('btn-submit').disabled = !ok;
}

// ─── gerar JSON ───────────────────────────────────────────────────────────────
function submitPedido() {
  const blocos = [];

  for (let i = 0; i < numBlocos; i++) {
    const laminas = [];
    document.querySelectorAll(`#laminas-${i} .lamina-row`).forEach(row => {
      const [, b, l] = row.id.split('-');
      laminas.push({
        cor:     parseInt(getVal(`lamina-${b}-${l}-cor`)),
        padrao:  parseInt(getVal(`lamina-${b}-${l}-padrao`) || '0'),
        posicao: parseInt(getVal(`lamina-${b}-${l}-posicao`))
      });
    });

    blocos.push({
      cor:     parseInt(getVal(`bloco-${i}-cor`)),
      andar:   i + 1,
      estoque: { id: parseInt(getVal(`bloco-${i}-estoque`)) },
      laminas
    });
  }

  const payload = {
    ordemDeProducao: parseInt(getVal('ordemProducao')),
    status: 1,
    tipo:   parseInt(getVal('tipoPedido')),
    corTampa: parseInt(getVal('corTampa')),
    blocos
  };

  const out = document.getElementById('json-output');
  out.textContent = JSON.stringify(payload, null, 2);
  out.classList.remove('hidden');

  const toast = document.getElementById('toast');
  toast.textContent = 'Pedido cadastrado com sucesso e enviado para a fila de produção!';
  toast.classList.remove('hidden');
  setTimeout(() => toast.classList.add('hidden'), 4000);

  console.log(payload);

  fetch('/api/pedidos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
  .then(r => r.json())
  .then(data => console.log('Pedido criado:', data))
  .catch(err => alert('Erro ao salvar: ' + err));
}