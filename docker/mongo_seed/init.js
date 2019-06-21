db = db.getSiblingDB('fantasy_db');

// insert your initial data here
db.teams.insert(
    [
        {
            "_id": ObjectId("5d0c18c190d2b33ae629aaa7"),
            "name": "HingleMcCringleberry",
            "players": [
                {
                    "name": "Nick Chubb",
                    "score": 0
                },
                {
                    "name": "James Conner",
                    "score": 0
                },
                {
                    "name": "Julio Jones",
                    "score": 0
                },
                {
                    "name": "Michael Thomas",
                    "score": 0
                }
            ],
            "total_score": 0
        },
        {
            "_id": ObjectId("5d0c18c190d2b33ae629aaa8"),
            "name": "rustezzies",
            "players": [
                {"name": "David Johnson", "score": 0},
                {"name": "Chris Carson", "score": 0},
                {"name": "JuJu Smith-Schuster", "score": 0},
                {"name": "DeAndre Hopkins", "score": 0}
            ],
            "total_score": 0
        }
    ]
);