FROM nokamoto13/webdriver-scala:0.0.0

ARG VERSION
ARG APP=webpush-testing-service

COPY target/universal/${APP}-${VERSION}.tgz .

RUN tar -zxvf ${APP}-${VERSION}.tgz && mv ${APP}-${VERSION} ${APP}

RUN rm ${APP}-${VERSION}.tgz

COPY entrypoint.sh entrypoint.sh

ENTRYPOINT [ "./entrypoint.sh", "webpush-testing-service/bin/webpush-testing-service" ]
