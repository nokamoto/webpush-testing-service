# webpush-testing-service

[![CircleCI](https://circleci.com/gh/nokamoto/webpush-testing-service.svg?style=svg)](https://circleci.com/gh/nokamoto/webpush-testing-service)

[![overview](https://user-images.githubusercontent.com/4374383/46121454-b97cc680-c24e-11e8-9fe3-f72880c250b2.png)](https://user-images.githubusercontent.com/4374383/46121454-b97cc680-c24e-11e8-9fe3-f72880c250b2.png)


- [Usage](#usage)
  - [Run](#run)
  - [Test](#test)
- [API](#api)
  - [POST /testing](#post-testing)
  - [GET /testing/:id](#get-testingid)
  - [DELETE /testing/:id](#delete-testingid)

## Usage
### Run

```
docker run -p 9000:9000 nokamoto13/webpush-testing-service:0.0.0
```

This will start the service with the default [applicationServerKey](https://github.com/nokamoto/webpush-testing-service/blob/741f437503b55427cca71628e29ac0440fb3d268/conf/application.conf#L8).

To configure _applicationServerKey_, pass `-DapplicationServerKey=${YOUR_VAPID_PUBLIC_KEY}` to the docker command line argument.

```
docker run -p 9000:9000 nokamoto13/webpush-testing-service:0.0.0 -DapplicationServerKey=${YOUR_VAPID_PUBLIC_KEY}
```

### Test

1. Call `POST /testing` to start a new browser. This API returns _id_ and _subscription_.

    **`POST http://localhost:9000/testing`**
    ```json
    {
      "id":"f6cdb052-c412-426f-bcd6-3cc54b8a3903",
      "subscription":{
        "endpoint":"https://updates.push.services.mozilla.com/wpush/v2/...",
        "auth":"MTYemr59rcxqaODIf3byeA==",
        "p256dh":"BL/DHX+aV4dlVwQG2YE/w7VogSJj+lJZvNRNiHoL+OTPKjhxZCXomNeUNLEkuLvt8SVGvnec+gn/JNQ1fYyjdP0=",
      }
    }
    ```

2. Send a webpush message with your client. The message **must** be JSON and have _id_ field.

    **`POST https://updates.push.services.mozilla.com/wpush/v2/...`**
    ```json
    {
      "id": "f6cdb052-c412-426f-bcd6-3cc54b8a3903",
      "options": {
        "body": "test message"
      }
    }
    ```

    The service worker will eventually receive the message from the push service (i.e. mozilla) and store the event to the testing service.

3. Call `GET /testing/:id` to retrieve the message.

    **`GET http://localhost:9000/testing/f6cdb052-c412-426f-bcd6-3cc54b8a3903`**
    ```json
    {
      "events":[
        "{\"id\":\"f6cdb052-c412-426f-bcd6-3cc54b8a3903\",\"options\":{\"body\":{\"test message\"}}}"
      ]
    }
    ```

[Here](https://github.com/nokamoto/webpush-scala/blob/e735375fce8643acb67389844c43c1389573d598/src/test/scala/com/github/nokamoto/webpush/WebpushTestingServiceSpec.scala) is a concrete example test code in Scala.

### API
#### POST /testing
Start a new browser.

| Status |
| --- |
| 201 |

| Response JSON Field | |
| --- | --- |
| id | an unique suite id |
| subscription.endpoint | [PushSubscription.endpoint](https://www.w3.org/TR/push-api/#dom-pushsubscription-endpoint) |
| subscription.auth | [PushSubscription.getKey('auth')](https://www.w3.org/TR/push-api/#dom-pushencryptionkeyname-p256dh) |
| subscription.p256dh | [PushSubscription.getKey('p256dh')](https://www.w3.org/TR/push-api/#dom-pushencryptionkeyname-auth) |

#### GET /testing/:id
Retrieve webpush messages that the browser received.

| Status |
| --- |
| 200 |
| 404 if `:id` not found |

| Response JSON Field | |
| --- | --- |
| events | a list of [PushEvent.data.text()](https://developer.mozilla.org/ja/docs/Web/API/PushEvent/data) |

#### DELETE /testing/:id
Quit the browser.

| Status |
| --- |
| 204 |
