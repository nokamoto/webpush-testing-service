this.onpush = function(event) {
    console.log('onpush: ' + event.data.text());

    const title = "webpush-testing-service";
    const options = event.data.json();

    const api = "http://" + self.location.hostname + "/testing/logs.json";
    const log = {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: {event: event.data()}
    };

    fetch(api, log)
        .then(function(res) { return res.text() })
        .then(function(txt) { console.log(txt) })
        .catch(function(e) { console.log(e) });

    event.waitUntil(self.registration.showNotification(title, options));
};
