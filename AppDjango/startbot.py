import sys
import tweepy
import logging
import time

from streetfight.bottwitter.config import create_api
from streetfight.bottwitter.get_tweets import get_tweets
from streetfight.bottwitter.favretweet import FavRetweetListener

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger()

def main(keywords):
	api = create_api()
	while True:
		try:
			get_tweets(api)
			# tweets_listener = FavRetweetListener(api)
			# stream = tweepy.Stream(api.auth, tweets_listener)
			# stream.filter(track=keywords, languages=["es"])
			logger.info("Acabe todas mis tareas programadas,estoy descansando por 60 segundos :)")
			time.sleep(60)
		except KeyboardInterrupt:
			sys.exit()

if __name__ == "__main__":
	main(["Street Fight", "La Coruña", "A Coruña","Galicia","la coruña","a coruña","galicia",
	"street fight"])
