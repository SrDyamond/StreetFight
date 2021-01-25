import sys
import tweepy
import logging
import time

from streetfight.bottwitter.config import create_api
from streetfight.bottwitter.get_tweets import get_tweets

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger()

def main():
	api = create_api()
	while True:
		try:
			get_tweets(api)
			logger.info("Estoy dormido...ZzZzZzZ")
			time.sleep(60)
		except KeyboardInterrupt:
			sys.exit()

if __name__ == "__main__":
	main()
