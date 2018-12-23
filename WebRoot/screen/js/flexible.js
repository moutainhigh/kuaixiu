function rem() {
    const w = document.documentElement.offsetWidth;
    document.documentElement.style.fontSize = w / 320 * 100 +'px';
}
rem();
window.onresize = rem;