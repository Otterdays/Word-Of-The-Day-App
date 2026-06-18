"""Curated lemma banks for §8d MVP category gap fill.

At least 36 unique lemmas per (category × tier). Tiers:
  0 pre_k/kindergarten | 1 grade_1/2 | 2 grade_3/4 | 3 grade_5/6
  4 grade_7/8 | 5 grade_9/10 | 6 grade_11/12 | 7 adult
"""
from __future__ import annotations

MVP_FILL_CATEGORIES = ("TECH", "SPORTS", "FOOD", "SCIENCE", "ANIMALS")

# (word, partOfSpeech) ordered easy → advanced within each category
_CATEGORY_LEXICON: dict[str, list[tuple[str, str]]] = {
    "TECH": [
        ("screen", "noun"), ("plug", "noun"), ("tap", "verb"), ("click", "verb"),
        ("phone", "noun"), ("tablet", "noun"), ("camera", "noun"), ("button", "noun"),
        ("game", "noun"), ("robot", "noun"), ("charger", "noun"), ("cable", "noun"),
        ("wifi", "noun"), ("email", "noun"), ("keyboard", "noun"), ("mouse", "noun"),
        ("password", "noun"), ("browser", "noun"), ("download", "verb"), ("upload", "verb"),
        ("software", "noun"), ("hardware", "noun"), ("network", "noun"), ("server", "noun"),
        ("database", "noun"), ("algorithm", "noun"), ("debug", "verb"), ("variable", "noun"),
        ("function", "noun"), ("encryption", "noun"), ("firewall", "noun"), ("bandwidth", "noun"),
        ("API", "noun"), ("framework", "noun"), ("deployment", "noun"), ("latency", "noun"),
        ("microservice", "noun"), ("blockchain", "noun"), ("authentication", "noun"),
        ("scalability", "noun"), ("abstraction", "noun"), ("recursion", "noun"),
        ("concurrency", "noun"), ("serialization", "noun"), ("REST", "noun"),
        ("GraphQL", "noun"), ("OAuth", "noun"), ("TLS", "noun"), ("sharding", "noun"),
        ("observability", "noun"), ("heuristic", "noun"), ("deterministic", "adjective"),
        ("memoization", "noun"), ("consensus", "noun"), ("CRDT", "noun"),
        ("homomorphic encryption", "noun"), ("chaos engineering", "noun"), ("SRE", "noun"),
        ("Halting problem", "noun"), ("lambda calculus", "noun"), ("Fourier transform", "noun"),
        ("speculative execution", "noun"), ("linearizability", "noun"), ("event sourcing", "noun"),
        ("zero-knowledge proof", "noun"), ("post-quantum cryptography", "noun"),
        ("WebAssembly", "noun"), ("computational irreducibility", "noun"),
        ("alignment problem", "noun"), ("Goodhart's law", "noun"), ("Moore's law", "noun"),
        ("surveillance capitalism", "noun"), ("digital sovereignty", "noun"),
        ("platform engineering", "noun"), ("privacy by design", "noun"),
        ("technological singularity", "noun"), ("path dependence", "noun"),
        ("vendor lock-in", "noun"), ("interoperability", "noun"),
        ("edge computing", "noun"), ("machine learning", "noun"), ("neural network", "noun"),
        ("technical debt", "noun"), ("Infrastructure as Code", "noun"),
        ("quantum", "adjective"), ("avatar", "noun"), ("pixel", "noun"), ("cache", "noun"),
        ("cookie", "noun"), ("malware", "noun"), ("phishing", "noun"), ("prototype", "noun"),
        ("sensor", "noun"), ("compiler", "noun"), ("repository", "noun"), ("patch", "noun"),
        ("router", "noun"), ("cloud", "noun"), ("settings", "noun"), ("update", "verb"),
        ("login", "verb"), ("logout", "verb"), ("backup", "noun"), ("virus", "noun"),
        ("link", "noun"), ("domain", "noun"), ("HTML", "noun"), ("CSS", "noun"),
        ("JavaScript", "noun"), ("open source", "adjective"), ("version", "noun"),
        ("interface", "noun"), ("loop", "noun"), ("compile", "verb"), ("index", "noun"),
        ("query", "noun"), ("schema", "noun"), ("runtime", "noun"), ("driver", "noun"),
        ("kernel", "noun"), ("IoT", "noun"), ("CDN", "noun"), ("telemetry", "noun"),
        ("sandbox", "noun"), ("middleware", "noun"), ("DevOps", "noun"), ("CI/CD", "noun"),
    ],
    "SPORTS": [
        ("run", "verb"), ("jump", "verb"), ("ball", "noun"), ("kick", "verb"), ("throw", "verb"),
        ("catch", "verb"), ("tag", "noun"), ("hop", "verb"), ("skip", "verb"), ("race", "noun"),
        ("goal", "noun"), ("team", "noun"), ("net", "noun"), ("bat", "noun"), ("hit", "verb"),
        ("swim", "verb"), ("dive", "verb"), ("skate", "verb"), ("bike", "verb"), ("toss", "verb"),
        ("pass", "verb"), ("shoot", "verb"), ("score", "verb"), ("win", "verb"), ("lose", "verb"),
        ("coach", "noun"), ("field", "noun"), ("court", "noun"), ("helmet", "noun"), ("glove", "noun"),
        ("stretch", "verb"), ("sprint", "verb"), ("marathon", "noun"), ("relay", "noun"),
        ("foul", "noun"), ("referee", "noun"), ("timeout", "noun"), ("halftime", "noun"),
        ("tournament", "noun"), ("champion", "noun"), ("underdog", "noun"), ("rivalry", "noun"),
        ("endurance", "noun"), ("stamina", "noun"), ("strategy", "noun"), ("offense", "noun"),
        ("defense", "noun"), ("lineup", "noun"), ("draft", "noun"), ("relegation", "noun"),
        ("promotion", "noun"), ("aggregate", "noun"), ("penalty", "noun"), ("overtime", "noun"),
        ("comeback", "noun"), ("upset", "noun"), ("seed", "noun"), ("bracket", "noun"),
        ("podium", "noun"), ("medal", "noun"), ("record", "noun"), ("doping", "noun"),
        ("biomechanics", "noun"), ("periodization", "noun"), ("VO2 max", "noun"),
        ("lactate threshold", "noun"), ("proprioception", "noun"), ("plyometrics", "noun"),
        ("salary cap", "noun"), ("free agency", "noun"), ("match fixing", "noun"),
        ("sportsmanship", "noun"), ("hooliganism", "noun"), ("paralympic", "adjective"),
        ("decathlon", "noun"), ("triathlon", "noun"), ("regatta", "noun"), ("slalom", "noun"),
        ("bobsled", "noun"), ("curling", "noun"), ("fencing", "noun"), ("archery", "noun"),
        ("equestrian", "adjective"), ("somersault", "noun"), ("cartwheel", "noun"),
        ("handstand", "noun"), ("warm-up", "noun"), ("cool-down", "noun"), ("hydration", "noun"),
        ("cramp", "noun"), ("concussion", "noun"), ("rehab", "noun"), ("scout", "noun"),
        ("playbook", "noun"), ("huddle", "noun"), ("touchdown", "noun"), ("home run", "noun"),
        ("hat trick", "noun"), ("ace", "noun"), ("deuce", "noun"), ("love", "noun"),
        ("checkmate", "noun"), ("pivot", "verb"), ("block", "verb"), ("dribble", "verb"),
        ("spike", "verb"), ("serve", "verb"), ("volley", "verb"), ("putt", "verb"),
        ("tee", "noun"), ("fairway", "noun"), ("bunker", "noun"), ("lap", "noun"),
        ("lane", "noun"), ("starting block", "noun"), ("finish line", "noun"),
    ],
    "FOOD": [
        ("apple", "noun"), ("milk", "noun"), ("bread", "noun"), ("egg", "noun"), ("rice", "noun"),
        ("soup", "noun"), ("cookie", "noun"), ("juice", "noun"), ("banana", "noun"), ("cheese", "noun"),
        ("water", "noun"), ("snack", "noun"), ("lunch", "noun"), ("breakfast", "noun"), ("dinner", "noun"),
        ("spoon", "noun"), ("fork", "noun"), ("plate", "noun"), ("bowl", "noun"), ("cup", "noun"),
        ("salt", "noun"), ("sugar", "noun"), ("honey", "noun"), ("butter", "noun"), ("toast", "noun"),
        ("salad", "noun"), ("pasta", "noun"), ("pizza", "noun"), ("taco", "noun"), ("sushi", "noun"),
        ("recipe", "noun"), ("bake", "verb"), ("boil", "verb"), ("grill", "verb"), ("steam", "verb"),
        ("chop", "verb"), ("slice", "verb"), ("peel", "verb"), ("season", "verb"), ("marinate", "verb"),
        ("simmer", "verb"), ("roast", "verb"), ("glaze", "verb"), ("knead", "verb"), ("ferment", "verb"),
        ("cuisine", "noun"), ("appetizer", "noun"), ("entree", "noun"), ("dessert", "noun"),
        ("garnish", "noun"), ("broth", "noun"), ("stock", "noun"), ("roux", "noun"),
        ("confit", "noun"), ("julienne", "verb"), ("blanch", "verb"), ("caramelize", "verb"),
        ("emulsify", "verb"), ("deglaze", "verb"), ("brine", "noun"), ("pickle", "verb"),
        ("allergy", "noun"), ("nutrition", "noun"), ("calorie", "noun"), ("protein", "noun"),
        ("carbohydrate", "noun"), ("vitamin", "noun"), ("fiber", "noun"), ("organic", "adjective"),
        ("preservative", "noun"), ("additive", "noun"), ("sustainable", "adjective"),
        ("farm-to-table", "adjective"), ("umami", "noun"), ("astringent", "adjective"),
        ("palate", "noun"), ("mouthfeel", "noun"), ("terroir", "noun"), ("mise en place", "noun"),
        ("haute cuisine", "noun"), ("street food", "noun"), ("food insecurity", "noun"),
        ("food desert", "noun"), ("locavore", "noun"), ("flexitarian", "noun"),
        ("macrobiotic", "adjective"), ("probiotic", "adjective"), ("gluten", "noun"),
        ("lactose", "noun"), ("halal", "adjective"), ("kosher", "adjective"),
        ("vegan", "adjective"), ("vegetarian", "adjective"), ("pescatarian", "adjective"),
        ("sous vide", "noun"), ("molecular gastronomy", "noun"), ("fermentation", "noun"),
        ("Maillard reaction", "noun"), ("sanitation", "noun"), ("HACCP", "noun"),
        ("cross-contamination", "noun"), ("shelf life", "noun"), ("expiration", "noun"),
        ("batch", "noun"), ("artisan", "adjective"), ("heirloom", "adjective"),
        ("forage", "verb"), ("infuse", "verb"), ("reduce", "verb"), ("proof", "verb"),
        ("zest", "noun"), ("citrus", "noun"), ("herb", "noun"), ("spice", "noun"),
        ("cinnamon", "noun"), ("cumin", "noun"), ("turmeric", "noun"), ("basil", "noun"),
        ("oregano", "noun"), ("thyme", "noun"), ("rosemary", "noun"), ("saffron", "noun"),
    ],
    "SCIENCE": [
        ("star", "noun"), ("moon", "noun"), ("sun", "noun"), ("rain", "noun"), ("cloud", "noun"),
        ("wind", "noun"), ("rock", "noun"), ("seed", "noun"), ("plant", "noun"), ("leaf", "noun"),
        ("root", "noun"), ("soil", "noun"), ("water", "noun"), ("ice", "noun"), ("heat", "noun"),
        ("light", "noun"), ("shadow", "noun"), ("magnet", "noun"), ("battery", "noun"), ("pulse", "noun"),
        ("germs", "noun"), ("habitat", "noun"), ("fossil", "noun"), ("volcano", "noun"), ("earthquake", "noun"),
        ("ecosystem", "noun"), ("gravity", "noun"), ("orbit", "noun"), ("phase", "noun"), ("atom", "noun"),
        ("molecule", "noun"), ("element", "noun"), ("compound", "noun"), ("reaction", "noun"),
        ("energy", "noun"), ("force", "noun"), ("motion", "noun"), ("friction", "noun"),
        ("density", "noun"), ("volume", "noun"), ("mass", "noun"), ("weight", "noun"),
        ("temperature", "noun"), ("pressure", "noun"), ("circuit", "noun"), ("current", "noun"),
        ("hypothesis", "noun"), ("experiment", "noun"), ("variable", "noun"), ("control", "noun"),
        ("data", "noun"), ("graph", "noun"), ("microscope", "noun"), ("telescope", "noun"),
        ("cell", "noun"), ("tissue", "noun"), ("organ", "noun"), ("DNA", "noun"), ("gene", "noun"),
        ("evolution", "noun"), ("adaptation", "noun"), ("mutation", "noun"), ("photosynthesis", "noun"),
        ("respiration", "noun"), ("digestion", "noun"), ("immune", "adjective"), ("vaccine", "noun"),
        ("bacteria", "noun"), ("virus", "noun"), ("fungus", "noun"), ("parasite", "noun"),
        ("biodiversity", "noun"), ("climate", "noun"), ("greenhouse", "noun"), ("carbon", "noun"),
        ("oxygen", "noun"), ("nitrogen", "noun"), ("periodic table", "noun"), ("ion", "noun"),
        ("catalyst", "noun"), ("enzyme", "noun"), ("homeostasis", "noun"), ("entropy", "noun"),
        ("quantum", "adjective"), ("relativity", "noun"), ("neutron", "noun"), ("proton", "noun"),
        ("electron", "noun"), ("isotope", "noun"), ("radiation", "noun"), ("spectrum", "noun"),
        ("wavelength", "noun"), ("frequency", "noun"), ("amplitude", "noun"), ("velocity", "noun"),
        ("acceleration", "noun"), ("momentum", "noun"), ("inertia", "noun"), ("equilibrium", "noun"),
        ("oxidation", "noun"), ("reduction", "noun"), ("polymer", "noun"), ("nanotechnology", "noun"),
        ("CRISPR", "noun"), ("epigenetics", "noun"), ("neuroscience", "noun"), ("astrophysics", "noun"),
        ("geology", "noun"), ("meteorology", "noun"), ("oceanography", "noun"), ("paleontology", "noun"),
        ("symbiosis", "noun"), ("extinction", "noun"), ("conservation", "noun"), ("sustainability", "noun"),
        ("peer review", "noun"), ("replication crisis", "noun"), ("falsifiability", "noun"),
        ("Occam's razor", "noun"), ("placebo", "noun"), ("double-blind", "adjective"),
    ],
    "ANIMALS": [
        ("cat", "noun"), ("dog", "noun"), ("bird", "noun"), ("fish", "noun"), ("frog", "noun"),
        ("bug", "noun"), ("bee", "noun"), ("worm", "noun"), ("duck", "noun"), ("cow", "noun"),
        ("pig", "noun"), ("horse", "noun"), ("sheep", "noun"), ("goat", "noun"), ("chicken", "noun"),
        ("rabbit", "noun"), ("mouse", "noun"), ("bear", "noun"), ("lion", "noun"), ("tiger", "noun"),
        ("elephant", "noun"), ("giraffe", "noun"), ("zebra", "noun"), ("monkey", "noun"), ("snake", "noun"),
        ("turtle", "noun"), ("whale", "noun"), ("dolphin", "noun"), ("shark", "noun"), ("octopus", "noun"),
        ("penguin", "noun"), ("owl", "noun"), ("eagle", "noun"), ("parrot", "noun"), ("bat", "noun"),
        ("deer", "noun"), ("wolf", "noun"), ("fox", "noun"), ("raccoon", "noun"), ("squirrel", "noun"),
        ("hedgehog", "noun"), ("kangaroo", "noun"), ("koala", "noun"), ("panda", "noun"), ("camel", "noun"),
        ("hippo", "noun"), ("rhino", "noun"), ("cheetah", "noun"), ("leopard", "noun"), ("hyena", "noun"),
        ("antelope", "noun"), ("bison", "noun"), ("moose", "noun"), ("elk", "noun"), ("beaver", "noun"),
        ("otter", "noun"), ("seal", "noun"), ("walrus", "noun"), ("crab", "noun"), ("lobster", "noun"),
        ("jellyfish", "noun"), ("coral", "noun"), ("butterfly", "noun"), ("moth", "noun"),
        ("dragonfly", "noun"), ("spider", "noun"), ("scorpion", "noun"), ("lizard", "noun"),
        ("gecko", "noun"), ("chameleon", "noun"), ("iguana", "noun"), ("salamander", "noun"),
        ("newt", "noun"), ("toad", "noun"), ("heron", "noun"), ("flamingo", "noun"), ("pelican", "noun"),
        ("swan", "noun"), ("goose", "noun"), ("turkey", "noun"), ("rooster", "noun"), ("hamster", "noun"),
        ("guinea pig", "noun"), ("ferret", "noun"), ("chinchilla", "noun"), ("alpaca", "noun"),
        ("llama", "noun"), ("migratory", "adjective"), ("nocturnal", "adjective"), ("diurnal", "adjective"),
        ("carnivore", "noun"), ("herbivore", "noun"), ("omnivore", "noun"), ("predator", "noun"),
        ("prey", "noun"), ("camouflage", "noun"), ("hibernation", "noun"), ("migration", "noun"),
        ("metamorphosis", "noun"), ("ecosystem", "noun"), ("endangered", "adjective"),
        ("domesticated", "adjective"), ("wildlife", "noun"), ("habitat", "noun"), ("pack", "noun"),
        ("herd", "noun"), ("flock", "noun"), ("school", "noun"), ("colony", "noun"),
        ("symbiosis", "noun"), ("parasite", "noun"), ("host", "noun"), ("vertebrate", "noun"),
        ("invertebrate", "noun"), ("mammal", "noun"), ("reptile", "noun"), ("amphibian", "noun"),
        ("anthropomorphism", "noun"), ("biodiversity", "noun"), ("invasive species", "noun"),
        ("keystone species", "noun"), ("apex predator", "noun"), ("food chain", "noun"),
    ],
}

_GLOSS: dict[str, dict[str, str]] = {
    "TECH": {
        "screen": "a flat surface that shows pictures or text on a device",
        "plug": "a connector that links a device to electrical power",
        "tap": "to touch a screen lightly to control it",
        "click": "to press a button or pointer to select something",
        "phone": "a handheld device for calls and apps",
        "tablet": "a portable touch-screen computer",
        "camera": "a device part that captures photos or video",
        "button": "a control you press to trigger an action",
        "game": "interactive software meant for play",
        "robot": "a machine that moves and follows instructions",
        "algorithm": "a step-by-step procedure for solving a problem",
        "encryption": "scrambling data so only authorized readers understand it",
        "blockchain": "a shared tamper-resistant ledger of linked records",
        "scalability": "ability to handle growth without failing",
        "abstraction": "hiding complex details behind a simpler interface",
        "surveillance capitalism": "profit model based on predicting and shaping behavior",
        "Moore's law": "observation that chip density historically doubled on a cadence",
    },
    "SPORTS": {
        "run": "to move quickly on foot",
        "ball": "a round object used in many games",
        "team": "people who play together toward a shared goal",
        "marathon": "a long-distance running race",
        "endurance": "ability to keep exerting effort over time",
        "tournament": "a structured series of contests producing a winner",
        "biomechanics": "science of forces and motion in athletic movement",
        "sportsmanship": "fair and respectful conduct in competition",
    },
    "FOOD": {
        "apple": "a crisp tree fruit, often red or green",
        "milk": "a nutritious white liquid from mammals",
        "marinate": "to soak food in seasoned liquid before cooking",
        "confit": "food preserved by slow cooking in fat",
        "umami": "a savory fifth basic taste",
        "Maillard reaction": "browning reaction between amino acids and sugars when heated",
        "food desert": "an area with limited access to affordable nutritious food",
    },
    "SCIENCE": {
        "star": "a massive luminous sphere of plasma in space",
        "hypothesis": "a testable proposed explanation for observations",
        "photosynthesis": "process plants use to convert light into chemical energy",
        "homeostasis": "maintaining stable internal conditions in living systems",
        "entropy": "measure of disorder or spread of energy in a system",
        "CRISPR": "gene-editing technology using bacterial immune mechanisms",
        "falsifiability": "property of a claim that could be proven wrong by evidence",
    },
    "ANIMALS": {
        "cat": "a small furry pet that often hunts mice",
        "dog": "a loyal mammal commonly kept as a companion",
        "migration": "seasonal movement of animals to find resources",
        "camouflage": "coloration or behavior that helps an animal blend in",
        "apex predator": "a hunter at the top of a food chain",
        "keystone species": "an organism whose role disproportionately supports an ecosystem",
    },
}

_SYNONYMS: dict[str, list[str]] = {
    "algorithm": ["procedure", "method", "process"],
    "encryption": ["encoding", "ciphering"],
    "endurance": ["stamina", "persistence"],
    "hypothesis": ["prediction", "theory"],
    "homeostasis": ["balance", "equilibrium"],
    "camouflage": ["disguise", "concealment"],
    "scalability": ["expandability", "growth capacity"],
    "abstraction": ["simplification", "model"],
    "marinate": ["soak", "steep"],
    "biodiversity": ["variety of life", "species richness"],
}

_USAGE: dict[str, str] = {
    "algorithm": "Central to computer science and everyday recommendation systems.",
    "hypothesis": "Must be testable; revise if evidence contradicts it.",
    "endurance": "Applies to mental tasks as well as physical sports.",
    "encryption": "Protects data in transit and at rest when implemented well.",
    "homeostasis": "Failure of homeostasis often signals illness.",
    "Moore's law": "Use cautiously; physical limits now complicate the trend.",
    "surveillance capitalism": "Term popularized by scholar Shoshana Zuboff.",
    "CRISPR": "Raises ethical questions about germline editing.",
}


def _default_gloss(category: str, word: str, pos: str) -> str:
    if word in _GLOSS.get(category, {}):
        return _GLOSS[category][word]
    w = word.replace("-", " ")
    if pos == "verb":
        if w.endswith("e"):
            return f"to {w} as an action or process"
        return f"to {w} as an action or process"
    if pos == "adjective":
        return f"describing something related to {w}"
    return f"a term for {w} in the {category.lower()} domain"


def _format_definition(tier: int, gloss: str, word: str, pos: str) -> str:
    if tier == 0:
        if pos == "verb":
            core = gloss[3:] if gloss.startswith("to ") else gloss
            return f"You {word} when you {core.rstrip('.')}."
        if pos == "adjective":
            return f"A word that tells you something is like {word}."
        return f"{gloss[0].upper() + gloss[1:]}, in a simple way for little learners."
    if tier == 1:
        return gloss[0].upper() + gloss[1:] + "."
    if tier == 2:
        return gloss[0].upper() + gloss[1:] + ", common in school readings."
    if tier == 3:
        return gloss[0].upper() + gloss[1:] + ", useful in reports and discussions."
    if tier == 4:
        return gloss[0].upper() + gloss[1:] + "; important in intermediate study."
    if tier == 5:
        return gloss[0].upper() + gloss[1:] + ", often assessed on exams."
    if tier == 6:
        return gloss[0].upper() + gloss[1:] + ", frequent in advanced coursework."
    return gloss[0].upper() + gloss[1:] + "; employed in specialized or professional discourse."


def _format_example(tier: int, word: str, pos: str, category: str) -> str:
    samples = {
        "TECH": {
            0: f"We used the {word} in class today.",
            7: f"Engineers debated how {word} affects system design.",
        },
        "SPORTS": {
            0: f"We practice {word} at recess.",
            7: f"Analysts linked {word} to the team's late-season surge.",
        },
        "FOOD": {
            0: f"Mom helped us {word} before dinner." if pos == "verb" else f"We tasted {word} at lunch.",
            7: f"The chef explained how {word} shapes the menu's identity.",
        },
        "SCIENCE": {
            0: f"We saw {word} during our nature walk.",
            7: f"The paper modeled {word} across multiple datasets.",
        },
        "ANIMALS": {
            0: f"The {word} lives near our park.",
            7: f"Conservation plans must account for {word} in the region.",
        },
    }
    cat = samples.get(category, {})
    if tier in cat:
        return cat[tier]
    if tier <= 1:
        return cat.get(0, f"Today we learned about {word}.")
    if tier >= 6:
        return cat.get(7, f"Researchers published new findings on {word}.")
    return f"Our textbook chapter explained {word} clearly."


def _tier_slice(words: list[tuple[str, str]], tier: int, size: int = 38) -> list[tuple[str, str]]:
    if len(words) <= size:
        return list(words)
    step = max(1, (len(words) - size) // 7)
    start = min(tier * step, len(words) - size)
    return words[start : start + size]


def _build_entry(category: str, tier: int, word: str, pos: str) -> dict:
    gloss = _default_gloss(category, word, pos)
    entry: dict = {
        "word": word,
        "partOfSpeech": pos,
        "pronunciation": "",
        "definition": _format_definition(tier, gloss, word, pos),
        "example": _format_example(tier, word, pos, category),
    }
    if tier >= 4:
        key = word.lower()
        if key in _SYNONYMS:
            entry["synonyms"] = _SYNONYMS[key]
        elif word in _SYNONYMS:
            entry["synonyms"] = _SYNONYMS[word]
        else:
            entry["synonyms"] = [word.split()[0], "related term"]
        note = _USAGE.get(word) or _USAGE.get(key)
        if note:
            entry["usageNote"] = note
        else:
            entry["usageNote"] = f"Common in {category.lower()} topics at this reading level."
    return entry


def _build_banks() -> dict[str, dict[int, list[dict]]]:
    banks: dict[str, dict[int, list[dict]]] = {}
    for category in MVP_FILL_CATEGORIES:
        lexicon = _CATEGORY_LEXICON[category]
        banks[category] = {}
        for tier in range(8):
            slice_words = _tier_slice(lexicon, tier)
            banks[category][tier] = [
                _build_entry(category, tier, word, pos) for word, pos in slice_words
            ]
    return banks


LEMMA_BANKS: dict[str, dict[int, list[dict]]] = _build_banks()
