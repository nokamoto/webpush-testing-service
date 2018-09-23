const base64encode = function(b) {
    return btoa(String.fromCharCode.apply(null, new Uint8Array(b)));
};

const copyToClipboard = function(id) {
    return function() {
        document.getElementById(id).select();
        document.execCommand('copy');
    };
};

const reset = function(subscription) {
    var endpoint = null;
    var p256dh = null;
    var auth = null;

    if (subscription != null) {
        endpoint = subscription.endpoint;
        p256dh = base64encode(subscription.getKey('p256dh'));
        auth = base64encode(subscription.getKey('auth'));
    }

    document.getElementById('endpoint').value = endpoint;
    document.getElementById('p256dh').value = p256dh;
    document.getElementById('auth').value = auth;
};

window.addEventListener('load', function(){
    document.getElementById('copy-endpoint').onclick = copyToClipboard('endpoint');
    document.getElementById('copy-auth').onclick = copyToClipboard('auth');
    document.getElementById('copy-p256dh').onclick = copyToClipboard('p256dh');
});
