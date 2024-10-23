from locust import HttpUser, task, between

class LoadTestUser(HttpUser):
    wait_time = between(0.5, 2)  # Time between each task (in seconds)

    @task(2)  # Assign higher weight to "/" endpoint
    def index(self):
        self.client.get("/")

    @task(1)  # Lower weight for "/slow" to mimic fewer slow requests
    def slow_endpoint(self):
        self.client.get("/slow")
