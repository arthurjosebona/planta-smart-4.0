// ─── constantes ────────────────────────────────────────────────────────────────

const COR_CLASSES = ["color-VAZIO", "color-PRETO", "color-VERMELHO", "color-AZUL"];
const COR_VAZIA   = "VAZIO";

// ─── estado ────────────────────────────────────────────────────────────────────

let isDragging = false;

// ─── helpers ───────────────────────────────────────────────────────────────────

/** input hidden name="cores" dentro do .space */
function getCorInput(space) {
  return space.querySelector("input[name='cores']");
}

/** .block dentro do .space */
function getBlock(space) {
  return space.querySelector(".block");
}

// ─── seleção ───────────────────────────────────────────────────────────────────

function toggleSelect(space) {
  space.classList.toggle("selected");
}

function dragSelect(space) {
  space.classList.add("selected");
}

function changeBlockColor(corEnum) {
  document.querySelectorAll(".space.selected").forEach((space) => {
    getBlock(space).classList.remove(...COR_CLASSES);
    getBlock(space).classList.add("color-" + corEnum);
    getCorInput(space).value = corEnum;
    space.classList.remove("selected");
  });
}

// ─── limpar estoque ────────────────────────────────────────────────────────────

function cleanEstoque() {
  document.querySelectorAll(".view-estoque .space").forEach((space) => {
    getBlock(space).classList.remove(...COR_CLASSES);
    getBlock(space).classList.add("color-" + COR_VAZIA);
    getCorInput(space).value = COR_VAZIA;
    space.classList.remove("selected");
  });
}

document.addEventListener("mousedown", () => { isDragging = true;  });
document.addEventListener("mouseup",   () => { isDragging = false; });

document.querySelectorAll(".view-estoque .space").forEach((space) => {

  space.addEventListener("mousedown", (evt) => {
    if (evt.button === 0) toggleSelect(space);
  });

  space.addEventListener("mouseenter", () => {
    if (isDragging) dragSelect(space);
  });

});