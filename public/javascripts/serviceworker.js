this.onpush = function(event) {
    console.log('onpush: ' + event.data.text());

    const title = 'webpush-testing-service';
    const data = event.data.json();

    const api = self.location.protocol + '//' + self.location.host + '/testing/' + data.id + '/events';
    const log = {
        method: 'POST',
        headers: {'Content-Type': 'text/plain'},
        body: event.data.text()
    };

    console.log('onpush: ' + api);
    console.log(log);

    fetch(api, log)
        .then(function(res) { return res.text() })
        .then(function(txt) { console.log(txt) })
        .catch(function(e) { console.log(e) });

    event.waitUntil(self.registration.showNotification(title, data.options));
};
