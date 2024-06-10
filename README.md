# Goodsun Backend

## Description
Backend application for the goodsun application. The frontend deliveres the parameters for the calculation.

## License
This application is licensed under `CC BY-NC 4.0`. No commercial use. This source must be named when using or developing further.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/)

## Building and Running the Docker Image

1. **Clone the Repository**:
   ```sh
   git clone https://github.com/dhbw-goodsun/goodsun-backend.git
   cd your-repo

2. **Build the Docker Image**
   docker build -t goodsun-backend .

3. **Start the Docker Container**
   docker run -d --name goodun-backend -p 8080:8080 goodsun-backend
