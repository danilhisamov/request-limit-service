# request-limit-service
Service which is able to limit incoming requests from clients by ip

Use @RequestLimited annotation on method with HttpServletRequest method parameter 

# Build & Run

docker build -t danilkhisamov/rl-app .

docker run -p 8080:8080 danilkhisamov/rl-app