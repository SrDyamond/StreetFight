import tweepy
import logging
from config import create_api
import json


# Function to extract tweets
def get_tweets(username):

        logging.basicConfig(level=logging.INFO)
        logger = logging.getLogger()
        api = create_api()
        # 200 tweets to be extracted
        number_of_tweets=200
        tweets = api.user_timeline(screen_name=username)

        # Empty Array
        tmp=[]

        # create array of tweet information: username,
        # tweet id, date/time, text
        tweets_for_csv = [tweet.text for tweet in tweets] # CSV file created
        for j in tweets_for_csv:

            # Appending tweets to the empty array tmp
            tmp.append(j)

        # Printing the tweets
        print(tmp)


# Driver code
if __name__ == '__main__':
    get_tweets("Street Fight")
