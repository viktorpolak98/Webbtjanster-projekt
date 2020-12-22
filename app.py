import requests
import json

# response = requests.get("https://polisen.se/api/events?locationname=Göteborg;Angered")
# # response.json()
# # json(response.text)
# res = json.loads(response.text)
# print(res[0])
# # list1 = []
# # list1.append(response)
# # print(list1)
# # things = json.loads(response)
# # print(things)
# print(response.status_code)
# print(response.text)
q = input("sök ")
response = requests.get("http://polisen.se/api/events?locationname=" + q)
print(response.text)

