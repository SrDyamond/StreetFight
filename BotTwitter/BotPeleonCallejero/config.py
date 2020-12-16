import tweepy
import logging
import os

logger = logging.getLogger()

def create_api():
    auth = tweepy.OAuthHandler("lfcWo3XjbtVo0WRTCrGkhaaVE",
        "GaFuFQ0uKbhPYqWqNDmrPttM6LdfTZVNlSTUNnEXNHWweOUy25")
    auth.set_access_token("1338837146363375616-q43ZTHb9lqPcoK7oDtQ22ewfGsiuNE",
        "mHnwQigRHqJabpeikdmFDUfu2cGkMzeXJOpUznPqddyX0")

    api = tweepy.API(auth)
    try:
        api.verify_credentials()
    except Exception as e:
        logger.error("Error creating API", exc_info=True)
        raise e
    logger.info("API created")
    return api
