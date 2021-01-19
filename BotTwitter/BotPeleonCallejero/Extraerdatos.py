import tweepy
import logging
from config import create_api
import json
import time

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger()

# Function to extract tweets
def get_tweets(api):


    mentions = api.mentions_timeline()

    for mention in mentions:
        try:
            if (mention.text == "@StreetFightSP Funcionas?"):
                api.update_status("Soy un buen bot, mi creador es un genio @"+str(mention.user.screen_name))
        except tweepy.TweepError as error:
            if error.api_code == 187:
                print('duplicate message')
def main():
    api = create_api()
    while True:
        get_tweets(api)
        logger.info("Waiting...")
        time.sleep(60)

if __name__ == "__main__":
    main()
