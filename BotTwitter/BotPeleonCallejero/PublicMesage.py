import tweepy

# Authenticate to Twitter
auth = tweepy.OAuthHandler("lfcWo3XjbtVo0WRTCrGkhaaVE",
    "GaFuFQ0uKbhPYqWqNDmrPttM6LdfTZVNlSTUNnEXNHWweOUy25")
auth.set_access_token("1338837146363375616-q43ZTHb9lqPcoK7oDtQ22ewfGsiuNE",
    "mHnwQigRHqJabpeikdmFDUfu2cGkMzeXJOpUznPqddyX0")
# Create API object
api = tweepy.API(auth)

# Create a tweet
api.update_status("Hello Tweepy")
