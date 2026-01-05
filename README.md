# Software Engineering Project 1 & 2
## Group 1
## Team Members
* [Auri Laitinen](https://github.com/aruraruri)
* [Niko Mehiläinen](https://github.com/mehiis)
* [Samuel Sarimo](https://github.com/samuelms123)
## Description
This project is a small social media desktop application that works as a private chatroom/posting platform for you and your friends.
## Documentation
* [Vision](https://github.com/samuelms123/OTP-1/blob/main/documents/vision.md)
* [UI design](https://github.com/samuelms123/OTP-1/tree/main/documents/FigmaUI)
## Tech-stack
### Front-end:
* Java
* JavaFX
* Scene Builder
### Backend:
* MariaDb
* ORM/Jakarta Persistence API
* Java
* JWT and Bcrypt for authentication
* Docker
### Testing:
* Jenkins
* JUnit (integration- and unit testing)
* SonarQube
* JMeter

Java FX is used for the front-end. The backend is powered by MariaDB for data storage, with ORM/Jakarta Persistence API for database interactions. JAAS Authentication is implemented for secure user authentication. Java was chosen for front- and backend to have a more unified tech stack which will make testing more streamlined.

### Localization:

Localization was done for English, Farsi, Finnish and Japanese. Changing between languages is possible in login view (right top corner).

### Database:
**ERD**<br>
![Database ERD](docs/diagrams/db/erd.png)<br><br>
**RDB**<br>
![Database RDB](docs/diagrams/db/rdb.png)

### Activity diagram:
![Activity-Diagram](docs/diagrams/img/activity-diagram-modify-profile.png)

## Screenshots:
![Login SS](docs/screenshots/login-ss.png)<br><br>
![FEED SS](docs/screenshots/feed.jpg)
[View more screenshots...](/docs/screenshots)

## Installation

* Clone the repo: ``git clone https://github.com/samuelms123/OTP-1``

* ``cd OTP-1``

* ``cp .env.example .env``

* Fill in the .env file SALT_ROUNDS=5 and JWT_SECRET=DSIASIDUAHSDIHA3 or whatever you
  like

* Create new MariaDB database, script found in the docs -folder of the project

* Start the program in the root directory: ``mvn javafx:run``

