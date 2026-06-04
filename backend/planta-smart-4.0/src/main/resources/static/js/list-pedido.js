// ─── list-pedido.js ───────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('tbody tr').forEach(row => montarAcoes(row));
});

function montarAcoes(row) {
  const td    = row.querySelector('.td-acoes');
  const id     = row.dataset.id;
  if (!td || !id) return;

  const btn = document.createElement('button');
  btn.className   = 'btn-iniciar';
  btn.textContent = 'Iniciar';
  btn.onclick     = () => iniciarPedido(id, btn, row);

  td.appendChild(btn);
}

function iniciarPedido(id, btn, row) {
  btn.disabled    = true;
  btn.textContent = 'Aguarde...';

  fetch(`/api/pedidos/${id}/status`, { method: 'PUT' })
    .then(res => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return res.json();
    })
    .then(pedido => atualizarLinha(pedido, row))
    .catch(err => {
      console.error('Erro ao iniciar pedido:', err);
      btn.disabled    = false;
      btn.textContent = 'Iniciar';
      exibirErro(row, 'Falha ao atualizar o pedido.');
    });
}

function atualizarLinha(pedido, row) {
  // Atualiza o badge de status
  const badge = row.querySelector('.badge');
  if (badge && pedido.status) {
    const statusLower = pedido.status.toLowerCase();
    badge.className   = `badge badge-${statusLower}`;
    badge.textContent = capitalizar(statusLower);
  }

  // Atualiza o data-status da linha
  if (pedido.status) row.dataset.status = pedido.status;

  // Substitui o botão por um indicador visual de concluído
  const td = row.querySelector('.td-acoes');
  if (td) {
    td.innerHTML = '<span class="status-iniciado">✓ Iniciado</span>';
  }

  row.classList.add('row-atualizada');
  setTimeout(() => row.classList.remove('row-atualizada'), 2000);
}

function exibirErro(row, msg) {
  const td = row.querySelector('.td-acoes');
  if (!td) return;
  const err = document.createElement('span');
  err.className   = 'status-erro';
  err.textContent = msg;
  td.appendChild(err);
  setTimeout(() => err.remove(), 3000);
}

function capitalizar(str) {
  return str.charAt(0).toUpperCase() + str.slice(1);
}