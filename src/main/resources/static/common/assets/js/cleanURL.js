if (window.location.hash === "#_=_") {
    history.replaceState(null, document.title, window.location.href.split("#")[0]);
}