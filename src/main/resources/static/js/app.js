// Izohlar bo'limini ochish/yopish
document.addEventListener('click', (e) => {
    const btn = e.target.closest('[data-toggle-comments]');
    if (btn) {
        const box = document.getElementById(btn.dataset.target);
        box.classList.toggle('open');
        if (box.classList.contains('open')) box.querySelector('input[name=content]').focus();
    }
    // Ochiq menyularni tashqariga bosganda yopish
    document.querySelectorAll('details.user-menu[open], details.post-menu[open]').forEach((d) => {
        if (!d.contains(e.target)) d.removeAttribute('open');
    });
});

document.addEventListener('DOMContentLoaded', () => {
    // Suhbat oynasini oxirgi xabarga aylantirish
    const chat = document.querySelector('.chat-body');
    if (chat) chat.scrollTop = chat.scrollHeight;

    // "Nashr qilish" bosilganda yozish maydoniga fokus
    if (location.hash === '#composer') {
        const ta = document.querySelector('#composer textarea');
        if (ta) ta.focus();
    }

    // Matn maydoni yozilganda o'sib boradi
    document.querySelectorAll('textarea[data-autogrow]').forEach((ta) => {
        ta.addEventListener('input', () => {
            ta.style.height = 'auto';
            ta.style.height = ta.scrollHeight + 'px';
        });
    });
});

// Parolni ko'rsatish / yashirish
document.addEventListener('click', (e) => {
    const eye = e.target.closest('[data-toggle-password]');
    if (!eye) return;
    const input = eye.parentElement.querySelector('input');
    input.type = input.type === 'password' ? 'text' : 'password';
    eye.classList.toggle('on', input.type === 'text');
});
