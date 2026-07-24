import urllib.request
import json

url = 'https://api.sendgrid.com/v3/mail/send'
api_key = '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'
data = {
    'personalizations': [{'to': [{'email': 'luu14102005@gmail.com'}]}],
    'from': {'email': 'hoanien.00@gmail.com'},
    'subject': 'Test',
    'content': [{'type': 'text/plain', 'value': 'Test'}]
}
req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers={
    'Authorization': 'Bearer ' + api_key,
    'Content-Type': 'application/json'
})
try:
    with urllib.request.urlopen(req) as response:
        print("SendGrid Success:", response.status)
except Exception as e:
    print("SendGrid Error:", e)
    if hasattr(e, 'read'):
        print(e.read().decode())
