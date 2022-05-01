Gravie Software Engineer Challenge
Instructions
After completing the challenge below, please send us an email with the location of your repository. If your repository is private, be sure to add us as collaborators so we can view your code.

Time Box
3-4 Hours

Synopsis
For this challenge you will implement the Giant Bomb API to create an application that will allow a user to search games and "rent" them. The application should consist of at least two unique pages (search and checkout). Your view should display the game thumbnail and title, and the rest is up to you. You can use any language and or framework you'd like.

Giant Bomb

You can get started by signing up for an API key here.

Resources
You can find the quickstart guide here.

You can find a full list of API features here.

Questions
Don't hesitate to reach out with any questions. Remember we are more focused on seeing your development process than checking off a list of requirements, so be sure you are able to speak to your code and your thoughts behind it.





###To run:

Add a file named `api.key` to the root of this project containing your api key. One can be found here:
https://www.giantbomb.com/forums/api-developers-3017/quick-start-guide-to-using-the-api-1427959/

This project is meant to input/output test the giantbomb api against clojure spec. It currently only
tests the /game part of their endpoint, and is more of a proof of concept. It has only been run 200
or so times against their data because they have a warning asking people to kindly not scrape them,
so what this project lacks in robustness it makes up for with not having angered giantbomb. Currently
the test is configured to hit their endpoint just 10 times, you can uncomment the 200 in source and 
test things more thoroughly, but you probably shouldn't do that more than once. I recommend setting a
seed. 

Run testing via: `clj -X:test`
