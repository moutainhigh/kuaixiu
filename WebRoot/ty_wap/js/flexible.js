function rem() {
    const w = document.documentElement.offsetWidth;
    document.documentElement.style.fontSize = w / 375 * 100 +'px';
}
rem();
window.onresize = rem;
