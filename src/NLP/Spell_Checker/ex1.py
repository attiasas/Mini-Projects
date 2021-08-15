import re, random, math, collections,nltk
from nltk import ngrams

# lemmatization imports
from nltk.stem import WordNetLemmatizer
nltk.download('wordnet')

class Spell_Checker:
    """
        The class implements a context sensitive spell checker. The corrections
        are done in the Noisy Channel framework, based on a language model and
        an error distribution model.
    """

    def __init__(self, lm=None):
        """
            Initializing a spell checker object with a language model as an
            instance  variable.

            Args:
                lm: a language model object. Defaults to None
        """
        self.lm = lm
        self.error_tables = None

        self.cache_edits1 = {}
        self.cache_edits2 = {}
        self.cache_candidates = {}

    def add_language_model(self, lm=None):
        """
            Adds the specified language model as an instance variable.
            (Replaces an older LM dictionary if set)

            Args:
                lm: a Spell_Checker.Language_Model object
        """
        self.lm = lm

    def add_error_tables(self, error_tables):
        """
            Adds the speficied dictionary of error tables as an instance variable.
            (Replaces an older value disctionary if set)

            Args:
                error_tables (dict): a dictionary of error tables in the format
                returned by  learn_error_tables()
        """
        self.error_tables = error_tables

    def evaluate(self, text):
        """
            Returns the log-likelihod of the specified text given the language
            model in use. Smoothing should be applied on texts containing OOV words
    
           Args:
               text (str): Text to evaluate.
    
           Returns:
               Float. The float should reflect the (log) probability.
        """
        if self.lm is None:
            return 0.0

        return self.lm.evaluate(text)

    def edits1(self, word):
        """
        generate all the edits that are 1 edit away from a given 'word'
        edits created by 4 operations: insertion, transposition, substitution, deletion.
        edits operations substitution and deletion try to add an alphabetic char ([a-z]) or space char
        :param word: str, a word (token) to generate edits from
        :return:
            edits: set of tuples, each tuple holds: (edited_word, (error_type,error))
        """

        if word in self.cache_edits1:
            return self.cache_edits1[word]

        letters = 'abcdefghijklmnopqrstuvwxyz '
        splits = [(word[:i], word[i:]) for i in range(len(word) + 1)]

        deletes = [(L + R[1:], ('insertion', L[len(L) - 1] + R[0] if len(L) > 0 else '#' + R[0])) for L, R in splits if R]
        transposes = [(L + R[1] + R[0] + R[2:], ('transposition', R[0] + R[1])) for L, R in splits if len(R) > 1]
        replaces = [(L + c + R[1:], ('substitution', R[0] + c)) for L, R in splits if R for c in letters if c != R[0]]
        inserts = [(L + c + R, ('deletion', L[len(L) - 1] + c if len(L) > 0 else '#' + c)) for L, R in splits for c in letters]

        edits = set(deletes + transposes + replaces + inserts)
        self.cache_edits1[word] = edits

        return edits

    def edits2(self, word,edits1=None):
        """
        generate all the edits that are 2 edit away from a given 'word' and his list of edits that are 1 edit away
        edits created by 4 operations: insertion, transposition, substitution, deletion.
        edits operations substitution and deletion try to add an alphabetic char ([a-z]) or space char
        :param word: str, a word (token) to generate edits from
        :param edits1: list/set, edits that are 1 edit away from the given word - for efficiency, if not provided it will be generated
        :return:
            edits: set of tuples, each tuple holds: (edited_word, (error_type1,error1,error_type2,error2))
        """

        if word in self.cache_edits2:
            return self.cache_edits2[word]

        all = set()

        if edits1 is None:
            edits1 = self.edits1(word)

        for (e1, info1) in edits1:
            for (e2, info2) in self.edits1(e1):
                # reject opposite operations:
                #  double substitution: swim -> wsim -> swim
                #  delete + insert: swim -> sim -> swim or swim -> swwim -> swim
                if e2 != word:
                    all.add((e2, info1+info2))

        self.cache_edits2[word] = all

        return all

    def known(self, words):
        """
        return a sublist of the given word list of all the words that the LM knows (1-gram tokens only)
        :param words: list, contains 1-gram tokens to check if known
        :return: list, sublist of list 'words', contains 1-gram tokens that the LM knows
        """
        return [(w, info) for (w, info) in words if self.lm.known(w)]

    def candidates(self, word):
        """
        generate all the candidates of a given word (1-gram token), candidates are all tokens that are 0-2 edit distance
        away from it.
        edits created by 4 operations: insertion, transposition, substitution, deletion.
        edits operations substitution and deletion try to add an alphabetic char ([a-z]) or space char
        :param word: str, a word (token) to generate edits from
        :return:
            edits: set of tuples, each tuple holds: (edited_word, error_info_tuple)
                   * error_info_tuple = a tuple that holds the information on the operations preformed on the given word
                   * in order to generate the edited_word (for efficiency), this tuple format changes base on the edit_dist
                   * * edit_dist = 0 --> error_info_tuple = None
                   * * edit_dist = 1 --> error_info_tuple = (error_type,error)
                   * * edit_dist = 2 --> error_info_tuple = (error_type1,error1,error_type2,error2)
        """
        if word in self.cache_candidates:
            return self.cache_candidates[word]

        edits0 = self.known([(word, None)])
        edits1 = self.edits1(word)
        edits2 = self.known(self.edits2(word,edits1))
        edits1 = self.known(edits1)

        all = set(edits0 + edits1 + edits2)
        self.cache_candidates[word] = all

        return all

    def get_normalizer_noisy_channel(self,error_info):
        """
        get the normalization value for a given error, sums all the counts of a given error in the error tables
        :param error_info: str, 2 consecutive chars that describes the edit (old and new char)
        :return:
            total_count: int, total counts in all the error tables that will be used as a normalizer
        """

        total = 0
        for error_type in self.error_tables:
            if error_info in self.error_tables[error_type]:
                total += self.error_tables[error_type][error_info]
        if total == 0:
            return 1

        return total

    def noisy_channel(self, candidate_info):
        """
        Calculate the simple noisy channel probability for a given error from the error table that
        :param candidate_info: a tuple that holds the information on the operations preformed on the given word
                               this tuple format changes base on the edit_dist
                               * edit_dist = 0 --> error_info_tuple = None
                               * edit_dist = 1 --> error_info_tuple = (error_type,error)
                               * edit_dist = 2 --> error_info_tuple = (error_type1,error1,error_type2,error2)
        :return:
            prob: float, noisy channel probability base on edits that occurred
        """
        total_prob = 1

        if candidate_info is not None:
            for i in range(0,len(candidate_info),2):
                error_type = candidate_info[i]
                error = candidate_info[i+1]

                if error not in self.error_tables[error_type]:
                    continue

                total_prob *= self.error_tables[error_type][error] / self.get_normalizer_noisy_channel(error)

        return total_prob

    def spell_check(self, text, alpha):
        """
        Returns the most probable fix for the specified text. Use a simple
        noisy channel model if the number of tokens in the specified text is
        smaller than the length (n) of the language model.

        Args:
            text (str): the text to spell check.
            alpha (float): the probability of keeping a lexical word as is.

        Return:
            A modified string (or a copy of the original if no corrections are made.)
        """

        if self.lm is None or self.error_tables is None:
            return text

        correction = text
        processed = normalize_text(text)

        n = self.lm.get_model_window_size()

        tokens = list(text_to_ngram(processed, 1))
        tokens = [i[0] for i in tokens]
        text_token_len = len(tokens)

        final_candidates = []

        start_range = 0
        end_range = text_token_len

        # priority to correct the first unknown word if exists
        for i in range(text_token_len):
            token = tokens[i]
            if not self.lm.known(token):
                start_range = i
                end_range = i + 1
        # generate best candidates
        for i in range(start_range,end_range):
            token = tokens[i]
            candidates = self.candidates(token)

            for candidate in candidates:
                w_candidate, info_candidate = candidate

                noisy = self.noisy_channel(info_candidate)
                alpha_score = alpha if token == w_candidate else (1 - alpha)

                score = alpha_score * noisy

                token_len = len(processed) if self.lm.generate_char_model else text_token_len

                if token_len >= n and score > 0.0: # apply context

                    n_tokens = tokens[:i] + [w_candidate] + tokens[i+1:]
                    candidate_ngram = ' '.join(n_tokens)

                    prob = math.pow(self.lm.log_base,self.evaluate(candidate_ngram))
                    score *= prob

                final_candidates.append((w_candidate, i, score))

        # correct to the arg with max score
        if len(final_candidates) > 0:
            (w_candidate, error_idx, score) = max(final_candidates,key=lambda x: x[2])
            tokens[error_idx] = w_candidate
            correction = ' '.join(tokens)

        return correction


    #####################################################################
    #                   Inner class                                     #
    #####################################################################

    class Language_Model:
        """
            The class implements a Markov Language Model that learns amodel from a given text.
            It supoprts language generation and the evaluation of a given string.
            The class can be applied on both word level and caracter level.
        """

        def __init__(self, n=3, chars=False):
            """
                Initializing a language model object.

                Args:
                    n (int): the length of the markov unit (the n of the n-gram). Defaults to 3.
                    chars (bool): True iff the model consists of ngrams of characters rather then word tokens.
                                  Defaults to False
            """

            if n <= 0:
                n = 1

            self.n = n
            self.generate_char_model = chars
            self.model_dict = None  # a dictionary of the form {ngram:count}, holding counts of all ngrams in the specified text.

            self.log_base = 2

            self.delimiter = ' '
            if chars:
                self.delimiter = ''

        def known(self, word):
            """
            checks if a given word (1-gram, not char) is known to the LM when trained (for efficiency)
            :param word: 1-gram word (not char) to check if the model known
            :return: true if the language model was trained on this word
            """
            return word in self.word_dict

        def build_model(self, text):
            """
                populates the instance variable model_dict.
                a dictionary of the form {ngram:count}, holding counts of all ngrams in the specified text.

                Args:
                    text (str): the text to construct the model from.
            """
            text = normalize_text(text)

            # populates
            self.model_dict = collections.Counter(text_to_ngram(text, self.n, self.generate_char_model))

            # create utils
            self.word_dict = set(collections.Counter(text_to_ngram(text, 1)).keys())
            self.word_dict = set([i[0] for i in self.word_dict])

            # create index of sample counts for each token base on aggregate distributions counts
            self.idx_distribution = []
            self.common_ordered_list = self.model_dict.most_common()

            for item in self.common_ordered_list:
                token, count = item
                self.idx_distribution.append(count)

        def get_model_dictionary(self):
            """
                Returns the dictionary class object
            """
            return self.model_dict

        def get_model_window_size(self):
            """
                Returning the size of the context window (the n in "n-gram")
            """
            return self.n

        def sample_ngram(self,k_ngram_prefix=None,min_count=1):
            """
            sample a random ngram from the model base on the ngram's distribution, and other complex filters
            :param k_ngram_prefix: tuple size of k, k <= n. filter and sample only from the ngram with the same prefix
            :param min_count: int >= 1, filter and sample only ngram with equal or greater count than the parameter
            :return:
                ngram: tuple, a randomly sampled ngram (base on counts) that agrees with all the filters provided
                       if none was found (None,None) will be returned
            """
            if k_ngram_prefix is None:
                # sample from all the distribution of ngrams
                return random.choices(self.common_ordered_list, weights=self.idx_distribution)[0]
            else:
                # sample from the distribution of ngrams that have the same k ngram prefix provided
                k = len(k_ngram_prefix)
                query_results = [(ngram, count) for (ngram, count) in self.common_ordered_list if count >= min_count and ngram[:k] == k_ngram_prefix]

                if len(query_results) <= 0:
                    return (None,None)

                weights = [count for (ngram, count) in query_results]
                return random.choices(query_results, weights=weights)[0]

        def generate(self, context=None, n=20,count_threshold=2):
            """
                Returns a string of the specified length, generated by applying the language model
                to the specified seed context. If no context is specified the context should be sampled
                from the models' contexts distribution. Generation should stop before the n'th word if the
                contexts are exhausted. If the length of the specified context exceeds (or equal to)
                the specified n, the method should return the a prefix of length n of the specified context.

                Args:
                    context (str): a seed context to start the generated string from. Defaults to None
                    n (int): the length of the string to be generated.
                    count_threshold (int): a minimum count threshold that a ngram has to have in order to be sampled
                Return:
                    String. The generated text.

            """
            if self.model_dict is None:
                return context

            if context is None:
                # generate first context
                (ngram, count) = self.sample_ngram(min_count=count_threshold)
                tokens_1gram = [i for i in ngram]
            else:
                context = normalize_text(context)
                tokens_1gram = list(self.text_to_ngram(context, 1, self.generate_char_model))
                tokens_1gram = [i[0] for i in tokens_1gram]

            if len(tokens_1gram) >= n:
                # case - return prefix
                return self.delimiter.join(tokens_1gram[:n])
            elif len(tokens_1gram) < self.n:
                # case - context has less than n-grams, sample to complete
                sub_ctx = tuple(tokens_1gram)
                (ngram, count) = self.sample_ngram(sub_ctx)
                if ngram is None:
                    return context
                tokens_1gram = [i for i in ngram]

            # generate
            for i in range(len(tokens_1gram) - (self.n - 1), n - (self.n - 1)):
                n_minus_1gram = tuple(tokens_1gram[i:i + (self.n - 1)])
                (best_ngram,count) = self.sample_ngram(n_minus_1gram,count_threshold)

                if best_ngram is None:
                    break

                tokens_1gram.append(best_ngram[len(best_ngram) - 1])

            return self.delimiter.join(tokens_1gram)

        def evaluate(self, text):
            """
                Returns the log-likelihood of the specified text to be a product of the model.
               Laplace smoothing should be applied if necessary.

               Args:
                   text (str): Text to evaluate.

               Returns:
                   Float. The float should reflect the (log) probability.
            """
            normalized = normalize_text(text)

            tokens = text_to_ngram(normalized, self.n, self.generate_char_model)

            likelihood = 0.0

            for ngram in tokens:
                prob = self.smooth(self.delimiter.join(ngram))
                likelihood += math.log(prob,self.log_base)
            return -math.inf if likelihood == 0.0 else likelihood

        def smooth(self, ngram):
            """
                Returns the smoothed (Laplace) probability of the specified ngram.

                Args:
                    ngram (str): the ngram to have it's probability smoothed

                Returns:
                    float. The smoothed probability.
            """

            if self.model_dict is None:
                return 0.0

            ngram = list(text_to_ngram(ngram,self.n,self.generate_char_model))

            if len(ngram) > 1 or len(ngram) == 0:
                return 0.0
            else:
                ngram = ngram[0]

            N = sum(self.model_dict.values())
            V = len(self.model_dict)

            if N + V == 0:
                return 0.0

            return (self.model_dict[ngram] + 1) / (N + V)

def text_to_ngram(text, n, char=False):
    """
        parse a given text into tokens of n-gram words/chars
        :param text: str, txt to parse
        :param n: int >= 1, number of grams in a single token
        :param char: bool, if true, the txt will be parse tokens as chars, otherwise txt will be parse tokens as words
        :return: zip of all the ngrams parsed as a tuple of 1-grams: for n=3 -->(gram1,gram2,gram3),(gram2,gram3,gram4)...
                 can be access in loop or cast to list/set/dict
    """

    processed_text = text

    if not char:
        regex = r'[\b\s\?!",\.\'/\\]'
        return ngrams([w for w in re.split(regex,processed_text) if w != ''],n)
    else:
        return ngrams([c for c in processed_text if c not in ['','\n','\t']], n)

def normalize_text(text,lemmatization=False):
    """
        Returns a normalized version of the specified string.

        No Entity Identification,punctuations corrections,or other complex operations will be preformed.
        this project will be focused on corrections of normal words (alphabetic), so for efficiency,
        scale and implementation time reasons, the following limitations were decided:

        * This project will only handle words any other chars will be discarded
        * This project will not treat text as multiple sentences but as a single unit, a stream of words/numbers

        the normalization operations that will be preformed in order to achieve it:

        1. lower all char
        2. strip from consecutive spaces
        3. remove punctuations, tags and non alphabetic chars
        4. lemmatization of words - this operation can be Controversial, it lowers the Vocabulary size so it can lead to
                                    improvements in running time but can also lower accuracy for some tasks
                                    so this operation will be optional (controlled by a flag - default to False)

        * tokenizing will be preformed in a different method
        (refactored for other outside usages to: text_to_ngram(txt,n,char))

        Args:
           text (str): the text to normalize
           lemmatization (bool): flag to determine if applying lemmatization on the text

        Returns:
            string. the normalized text.
    """
    # Case folding and striping
    processed = text.lower()

    # remove punctuations, tags and non alphabetic chars
    processed = re.sub(r"'s\b", "", processed)  # remove ending 's
    processed = re.sub(r'<[a-zA-Z0-9]+>', '', processed) # remove tags
    processed = re.sub(r'[^a-zA-Z]',' ',processed)

    # strip extra spaces
    processed = processed.strip()

    # Lemmatization - Porter stemmer
    if lemmatization:
        lemmatizer = WordNetLemmatizer()
        tokens = text_to_ngram(processed,1)
        processed_tokens = []
        for token_tuple in tokens:
            token = token_tuple[0]
            processed_tokens.append(lemmatizer.lemmatize(token))
        processed = ' '.join(processed_tokens)

    return processed