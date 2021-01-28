import tweepy
import requests

def search_all_tweet(question):
    auth = tweepy.OAuthHandler("kLBuI2XrILD4qIJ3vZhJY7KTf", "CpCEWCmbWVMWWuTVSvTrNEA9KgESonp1NEozgDSEjqPscItt1N")
    auth.set_access_token("1338824296177786883-mzQQKoz1p6YUclAkQIMScBweIKnbWg", "bSeaoJs8OPSav7bvbcA3K86kjA0VhskAfDHgnuq8o6gF1")
    api = tweepy.API(auth)
    api = tweepy.API(auth, wait_on_rate_limit=True)

    tweeters = []
    new_search = api.search(q=question, lang="sv", maxResults=1)
    for status in new_search:
        tweet_dict = {
        "username" : "",
        "text" : ""
        }
        tweet_dict["username"]=status.user.name
        tweet_dict["text"]=status.text
        tweeters.append(tweet_dict)
    
    return tweeters

def get_police():
    response = requests.get("https://polisen.se/api/events")
    print(response.headers['content-type'])
    res = response.json()
    return res

def search_police(question):
    response = requests.get("https://polisen.se/api/events?type=" + question)
    print(response.headers['content-type'])
    res = response.json()
    return res

def get_geo_locations(dict):
    geolocations = []
    for item in dict:
        geodict = {
        "id" : "",
        "longitude" : "",
        "latitude" : ""
        }
        a_dict = item["location"]
        a_id = item["id"]
        for cordinate in a_dict:
            a_list = a_dict["gps"].split(",")
            longitude = a_list[0]
            latitude = a_list[1]
            geodict["id"] = a_id
            geodict["longitude"] = longitude
            geodict["latitude"] = latitude
            geolocations.append(geodict)

    return geolocations

def search_tweet(question, longitude, latitude):
    auth = tweepy.OAuthHandler("kLBuI2XrILD4qIJ3vZhJY7KTf", "CpCEWCmbWVMWWuTVSvTrNEA9KgESonp1NEozgDSEjqPscItt1N")
    auth.set_access_token("1338824296177786883-mzQQKoz1p6YUclAkQIMScBweIKnbWg", "bSeaoJs8OPSav7bvbcA3K86kjA0VhskAfDHgnuq8o6gF1")
    api = tweepy.API(auth)
    api = tweepy.API(auth, wait_on_rate_limit=True)

    tweeters = []
    new_search = api.search(q=question, geocode=str(longitude + "," +  latitude + "," + "50km"), lang="sv", maxResults=1)
    for status in new_search:
        tweet_dict = {
        "username" : "",
        "text" : ""
        }
        tweet_dict["username"]=status.user.name
        tweet_dict["text"]=status.text
        tweeters.append(tweet_dict)
    
    return tweeters