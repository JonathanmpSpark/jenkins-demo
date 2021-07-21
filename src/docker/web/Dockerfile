FROM python:3
##FROM python:3-alpine
ENV PYTHONUNBUFFERED=1
##RUN apk add binutils libc-dev

WORKDIR /app

COPY ./src/requirements.txt /app/
RUN pip install -r requirements.txt --no-cache

COPY ./src/docker/web/start /start
RUN sed -i 's/\r$//g' /start

RUN chmod +x /start

COPY . /app/

CMD '/start'