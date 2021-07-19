FROM python:3
ENV PYTHONUNBUFFERED=1

WORKDIR /app

COPY requirements.txt /app/
RUN pip install -r requirements.txt --no-cache

COPY ./docker/web/start /start
RUN sed -i 's/\r$//g' /start

RUN chmod +x /start

COPY . /app/