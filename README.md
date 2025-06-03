# Static Frontend with Docker

This README explains how to run this static frontend using Docker.

## Prerequisites

* Docker installed on your machine. You can find the installation instructions for your operating system in the [official Docker documentation](https://docs.docker.com/get-docker/).

## How to Run

1.  **Access the front directory**

    From the root of the repository access the front end directory with:
    ```bash
    cd front
    ```

2.  **Build the Docker image:**
    In this directory of the frontend project, run the following command:
    ```bash
    docker build -t frontend .
    ```
3.  **Run the Docker container:**
    To run the frontend and make it accessible through a port on your local machine, use the following command:
    ```bash
    docker run -p 8080:80 frontend
    ```
    
4.  **Access the Frontend in your browser:**
    Open your web browser and go to the following address:
    ```
    http://localhost:8080
    ```