import tweepy
import logging
from config import create_api
import json
import time

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger()

# Function to extract tweets
def get_tweets(username,api):


        # 200 tweets to be extracted
        number_of_tweets=200
        tweets = api.user_timeline(screen_name=username)

        # Empty Array
        tmp=[]

        # create array of tweet information: username,
        # tweet id, date/time, text
        tweets_for_csv = [tweet.text for tweet in tweets] # CSV file created
        for j in tweets_for_csv:
            tmp.append(j)
        # Printing the tweets
        print(tmp)
def main():
    api = create_api()
    while True:
        get_tweets("Street Fight",api)
        logger.info("Waiting...")
        time.sleep(60)

if __name__ == "__main__":
    main()
