const form = document.querySelector('#players-form');
const board = document.querySelector('#board');
const status = document.querySelector('#status');
const hint = document.querySelector('#hint');
const error = document.querySelector('#error');
const start = document.querySelector('#start-button');
let game = null;
let busy = false;

async function request(path, body) {
  const response = await fetch(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
  const data = await response.json();
  if (!response.ok) throw new Error(data.error || 'The move could not be completed.');
  return data;
}

function render() {
  const cells = game ? game.cells : Array(9).fill(null);
  board.replaceChildren(...cells.map((mark, index) => {
    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'square';
    button.dataset.mark = mark || '';
    button.disabled = busy || !game || Boolean(mark) || game.status !== 'inProgress';
    button.setAttribute('aria-label', `Square ${index + 1}${mark ? `: ${mark}` : ': empty'}`);
    const content = document.createElement('span');
    content.className = mark ? '' : 'number';
    content.textContent = mark || index + 1;
    button.append(content);
    button.addEventListener('click', () => move(index));
    return button;
  }));
  start.disabled = busy;
  document.querySelectorAll('input').forEach(input => { input.disabled = busy; });
  start.textContent = game ? 'New game' : 'Start game';
  if (!game) return;

  document.querySelector('#players').hidden = false;
  game.players.forEach(player => {
    const mark = player.mark.toLowerCase();
    document.querySelector(`#${mark}-label`).textContent = player.name;
    document.querySelector(`#${mark}-player`).classList.toggle('active',
      game.status === 'inProgress' && game.currentPlayer.mark === player.mark);
  });
  const current = game.status === 'won' ? game.winner : game.currentPlayer;
  const turnMark = document.querySelector('#turn-mark');
  turnMark.textContent = game.status === 'draw' ? '=' : current.mark;
  turnMark.classList.toggle('o-turn', current.mark === 'O');
  status.textContent = game.status === 'won' ? `${game.winner.name} wins!`
    : game.status === 'draw' ? "It's a draw." : `${game.currentPlayer.name}'s turn`;
  hint.textContent = game.status === 'inProgress' ? 'Choose an empty square.'
    : 'Play again? Start a new game.';
}

async function perform(action) {
  if (busy) return;
  busy = true;
  error.textContent = '';
  render();
  try { game = await action(); }
  catch (failure) {
    error.textContent = failure instanceof TypeError
      ? 'Cannot reach the game. Check that the local server is running.' : failure.message;
  }
  finally { busy = false; render(); }
}

form.addEventListener('submit', event => {
  event.preventDefault();
  const xName = document.querySelector('#x-name').value;
  const oName = document.querySelector('#o-name').value;
  perform(() => request('/api/games', { xName, oName }));
});

function move(position) {
  const focusedSquare = position;
  perform(() => request(`/api/games/${game.id}/moves`, { position })).then(() => {
    const next = [...board.children].find((button, index) => index > focusedSquare && !button.disabled)
      || [...board.children].find(button => !button.disabled);
    if (next) next.focus({ preventScroll: true });
    else start.focus({ preventScroll: true });
  });
}

render();
