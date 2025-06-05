const toggleBtn = document.getElementById('modoToggle');
const icon = toggleBtn.querySelector('i');
const body = document.body;
const nav = document.getElementById('mainNav');  // captura el nav

// Función para actualizar estilos del botón y navbar según el modo
function actualizarBoton() {
  const modoClaro = body.classList.contains('light-mode');

  // Cambiar clases botón
  toggleBtn.classList.toggle('btn-outline-light', !modoClaro);
  toggleBtn.classList.toggle('btn-outline-dark', modoClaro);

  icon.classList.toggle('fa-moon', !modoClaro);
  icon.classList.toggle('fa-sun', modoClaro);

  // Cambiar clases navbar para modo claro / oscuro
  if(modoClaro) {
    nav.classList.remove('navbar-dark', 'bg-dark');
    nav.classList.add('navbar-light', 'bg-light');
  } else {
    nav.classList.remove('navbar-light', 'bg-light');
    nav.classList.add('navbar-dark', 'bg-dark');
  }
}

// Al cargar: aplica preferencia guardada
const savedMode = localStorage.getItem('modo');
if (savedMode === 'claro') {
  body.classList.add('light-mode');
}
actualizarBoton();

// Al hacer clic en el botón
toggleBtn.addEventListener('click', () => {
  body.classList.toggle('light-mode');
  const modoClaro = body.classList.contains('light-mode');
  localStorage.setItem('modo', modoClaro ? 'claro' : 'oscuro');
  actualizarBoton();
});
