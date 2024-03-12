# Verbatim Backend
https://verbatim-game.com/

## Description

This repository houses the backend code for our project.

## Architecture

In this project, we've chosen Spring Boot as our backend technology stack. Below is our current database structure.
![Verbatim_Updated_DB](https://github.com/dartmouth-cs98-23f/verbatim_backend/assets/76986782/a3ef231b-d6c1-490d-a6c1-baadfddac9c3)



## Setup

Here are the steps to follow in order to run the initial 'Verbatim Backend' code:

#### For IntelliJ IDEA (recommended)

1. Clone the 'verbatim_backend' repository.
2. After opening the repository in IntelliJ, navigate to the 'src/main/java/com/cs98/VerbatimBackend' directory.
3. Set up Run Configurations:
   - Run: ```spring-boot:run```
   - Working Directory: ```verbatim_backend```
   - VM Options: ```-Dspring.profiles.active=dev```
4. Connect to your Postgres database by entering fields in the ```application-dev.properties``` file:
   - ```spring.datasource.url```: something like ```jbdc:postgres://localhost:5432/verbatim``` (you should already have a postgres database created)
   - ```spring.datasource.username```: your postgres username
   - ```spring.datasource.password```: your postgres password
5. Click the play button in the upper right-hand corner to run the backend.

#### For VS Code

1. Clone the 'verbatim_backend' repository.
2. After opening the repository in Visual Studio Code, navigate to the 'src/main/java/com/cs98/VerbatimBackend' directory.
3. Install the Java extension 'Extension Pack for Java'.
4. Right-click on the 'VerbatimBackendApplication.java' file and select the 'Run Java' option.

## Deployment

The backend is deployed via Heroku. We are utilizing a PostgreSQL database, which is connected to the deployed version of our app as a Heroku resource.

## Authors

Ryan Dudak, Eve Wening, Eric Richardson, Dahlia Igiraneza, Jackline Gathoni

## Acknowledgments

- Eric Richardson (For not only creating the initial starter code for this backend project but also taking the lead in its overall setup.)
