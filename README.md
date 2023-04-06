
## MVVM Architecture is used

## Offline Database 
    - Room database is used for data store in the local database.
        - Room used sqlite3 database as base database to store the data and I choose room because it is easy to handle storing and fetching the data from database using room library.
## Api
    * Retrofit is used for api calls
        - It is choosed beacause it is best choice out there for api call handle.

## Features 
    - After login there is one logout button, which will be use for logout the user.
    - There are two text field which are not editable but when user click on 'select make' text field then a request will be sent to the api (if user first time clicking it) and fetch the data from the api and store it into local datbase and show all the makes to the user on a dialog. And next time if user click on this text field then it fetch makes from the local database.
    - There are two button ("Refresh" and "Add Car") when click on refresh it request the api and fetch data and store it to room and when click on add car button after selecting the make and model then it store your favorite car to room. and show that to Your Cars section right below to these button using recycler view.
    - User can also add image to it and delete the data from the room.
    - Design is also work in dark mode very well.
