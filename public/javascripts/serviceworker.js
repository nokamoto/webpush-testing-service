this.onpush = function(event) {
    console.log('onpush: ' + event.data.text());

    const title = "webpush-testing-service";
    const options = event.data.json();

    event.waitUntil(self.registration.showNotification(title, options));
}
