db = db.getSiblingDB('fantasy_db');

// insert your initial data here
db.teams.insert(
    [
        {
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
            "name": "rustezzies",
            "players": [
                {
                    "name": "David Johnson",
                    "score": 0
                },
                {
                    "name": "Chris Carson",
                    "score": 0
                },
                {
                    "name": "JuJu Smith-Schuster",
                    "score": 0
                },
                {
                    "name": "DeAndre Hopkins",
                    "score": 0
                }
            ],
            "total_score": 0
        }
    ]
);