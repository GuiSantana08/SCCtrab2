FROM node
WORKDIR /ROOT
COPY images /ROOT/images
COPY test-users.yml /ROOT
COPY test-rentals.yml /ROOT
COPY test-questions.yml /ROOT
COPY test-listing.yml /ROOT
COPY test-houses.yml /ROOT
COPY load-data.yml /ROOT
COPY test-utils.js /ROOT
RUN npm install artillery
RUN npm install node-fetch -save
RUN npm install https://github.com/preguica/artillery-plugin-metrics-by-endpoint.git
CMD DEBUG=http ./node_modules/.bin/artillery run --output test-users.json test-users.yml ; \
    ./node_modules/.bin/artillery report --output test-users.html test-users.json ; \
    ./node_modules/.bin/artillery run --output test-rentals.json test-rentals.yml ; \
    ./node_modules/.bin/artillery report --output test-rentals.html test-rentals.json ; \
    ./node_modules/.bin/artillery run --output test-questions.json test-questions.yml ; \
    ./node_modules/.bin/artillery report --output test-questions.html test-questions.json ; \
    ./node_modules/.bin/artillery run --output test-listing.json test-listing.yml ; \
    ./node_modules/.bin/artillery report --output test-listing.html test-listing.json ; \
    ./node_modules/.bin/artillery run --output test-houses.json test-houses.yml ; \
    ./node_modules/.bin/artillery report --output test-houses.html test-houses.json ; \
    ./node_modules/.bin/artillery run --output load-data.json load-data.yml ; \
    ./node_modules/.bin/artillery report --output load-data.html load-data.json ; \
    sleep 5000
