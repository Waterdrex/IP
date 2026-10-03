rec = {
    "name": "Ajay Nagar",
    "Age": 23,
    "Address": {
        "area": "Adarsh Nagar",
        "city": "Mumbai",
        "state": "Maharashtra"
    },
    "subject": ["c", "javascript", "java"]
}
console.log(rec.Address.state)
console.log(rec)
console.log("Name: " + rec["name"])
console.log("Name: " + rec.name)
console.log("Subject: " + rec.subject[2])