import smtplib
from email.message import EmailMessage

msg = EmailMessage()
msg.set_content("Test email")
msg['Subject'] = "Test"
msg['From'] = "hoanien.00@gmail.com"
msg['To'] = "luu14102005@gmail.com"

try:
    s = smtplib.SMTP('smtp.gmail.com', 587)
    s.starttls()
    s.login("hoanien.00@gmail.com", "xiaq hstw vqnn zmjv")
    s.send_message(msg)
    s.quit()
    print("SMTP Success")
except Exception as e:
    print("SMTP Error:", e)
