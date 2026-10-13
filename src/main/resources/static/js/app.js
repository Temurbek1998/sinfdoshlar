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

// Xabar (toast) bir necha soniyadan keyin yo'qoladi
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-toast]').forEach((t) => {
        setTimeout(() => t.classList.add('hide'), 4500);
        t.addEventListener('click', () => t.classList.add('hide'));
    });
});

function showToast(text) {
    const t = document.createElement('div');
    t.className = 'toast error';
    t.textContent = text;
    document.body.appendChild(t);
    setTimeout(() => t.classList.add('hide'), 4000);
}

// Fayl tanlanganda: hajmini tekshirish, rasmni oldindan ko'rsatish, avtomatik yuborish
document.addEventListener('change', (e) => {
    const input = e.target;
    if (input.type !== 'file' || !input.files.length) return;
    const file = input.files[0];
    const max = parseFloat(input.dataset.maxMb || '0');
    if (max && file.size > max * 1024 * 1024) {
        showToast(`Fayl juda katta: ${(file.size / 1048576).toFixed(1)} MB. Ruxsat etilgan: ${max} MB gacha.`);
        input.value = '';
        return;
    }
    if (input.hasAttribute('data-image-input')) {
        const url = URL.createObjectURL(file);
        const composer = input.closest('.composer');
        if (composer) {
            const box = composer.querySelector('.composer-preview');
            box.querySelector('img').src = url;
            box.hidden = false;
            composer.classList.add('has-image');
        }
        const drop = input.closest('.drop');
        if (drop) {
            const img = drop.querySelector('.drop-preview');
            img.src = url;
            img.hidden = false;
        }
    }
    if (input.hasAttribute('data-autosubmit')) input.form.submit();
});

document.addEventListener('click', (e) => {
    const clear = e.target.closest('[data-clear-image]');
    if (!clear) return;
    const composer = clear.closest('.composer');
    composer.querySelector('[data-image-input]').value = '';
    const box = composer.querySelector('.composer-preview');
    box.hidden = true;
    box.querySelector('img').removeAttribute('src');
    composer.classList.remove('has-image');
});

// Musiqa pleyeri
document.addEventListener('DOMContentLoaded', () => {
    const player = document.getElementById('player');
    if (!player) return;
    const audio = player.querySelector('audio');
    const tracks = [...document.querySelectorAll('.track[data-src]')];
    const seek = player.querySelector('[data-seek]');
    const volume = player.querySelector('[data-volume]');
    let index = -1;

    const fmt = (s) => {
        if (!isFinite(s)) return '0:00';
        s = Math.floor(s);
        return `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;
    };

    try { volume.value = localStorage.getItem('volume') || volume.value; } catch (_) { /* yo'q */ }
    audio.volume = volume.value;

    function load(i) {
        if (i < 0 || i >= tracks.length) return;
        tracks.forEach((t) => t.classList.remove('current', 'playing'));
        index = i;
        const t = tracks[i];
        t.classList.add('current');
        audio.src = t.dataset.src;
        player.querySelector('[data-title]').textContent = t.dataset.title;
        player.querySelector('[data-artist]').textContent = t.dataset.artist || 'Noma\'lum ijrochi';
        player.hidden = false;
        document.body.classList.add('player-on');
        audio.play().catch(() => {});
    }

    tracks.forEach((t, i) => {
        t.querySelector('.track-play').addEventListener('click', () => {
            if (i === index) { audio.paused ? audio.play() : audio.pause(); } else { load(i); }
        });
        // Davomiylikni oldindan bilib olish
        const probe = new Audio();
        probe.preload = 'metadata';
        probe.src = t.dataset.src;
        probe.addEventListener('loadedmetadata', () => {
            t.querySelector('[data-duration]').textContent = fmt(probe.duration);
            probe.removeAttribute('src');
        });
    });

    player.querySelector('[data-toggle]').addEventListener('click', () => {
        if (index < 0) return load(0);
        audio.paused ? audio.play() : audio.pause();
    });
    player.querySelector('[data-prev]').addEventListener('click', () => {
        if (audio.currentTime > 3) audio.currentTime = 0; else load(Math.max(0, index - 1));
    });
    player.querySelector('[data-next]').addEventListener('click', () => load((index + 1) % tracks.length));

    audio.addEventListener('play', () => {
        player.classList.add('playing');
        if (tracks[index]) tracks[index].classList.add('playing');
    });
    audio.addEventListener('pause', () => {
        player.classList.remove('playing');
        if (tracks[index]) tracks[index].classList.remove('playing');
    });
    audio.addEventListener('ended', () => { if (index + 1 < tracks.length) load(index + 1); });
    audio.addEventListener('loadedmetadata', () => {
        player.querySelector('[data-total]').textContent = fmt(audio.duration);
    });
    audio.addEventListener('timeupdate', () => {
        player.querySelector('[data-current]').textContent = fmt(audio.currentTime);
        if (audio.duration && !seek.matches(':active')) seek.value = (audio.currentTime / audio.duration) * 100;
    });
    seek.addEventListener('input', () => {
        if (audio.duration) audio.currentTime = (seek.value / 100) * audio.duration;
    });
    volume.addEventListener('input', () => {
        audio.volume = volume.value;
        try { localStorage.setItem('volume', volume.value); } catch (_) { /* yo'q */ }
    });
});
