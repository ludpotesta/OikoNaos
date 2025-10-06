const burger = document.querySelector('.burger');
const menu = document.getElementById('mobileMenu');

burger.addEventListener('click', () => {
    const open = menu.style.display === 'block';
    menu.style.display = open ? 'none' : 'block';
    burger.setAttribute('aria-expanded', String(!open));
});

document.addEventListener('click', (e) => {
    if (!menu.contains(e.target) && !burger.contains(e.target)) {
        menu.style.display = 'none';
        burger.setAttribute('aria-expanded', 'false');
    }
});