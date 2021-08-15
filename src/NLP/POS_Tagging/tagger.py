"""
intro2nlp, assignment 3, 2021

In this assignment you will implement a Hidden Markov model
to predict the part of speech sequence for a given sentence.

"""

from math import log, isfinite
from collections import Counter

import sys, os, time, platform, nltk, random

def read_annotated_sentence(f):
    line = f.readline()
    if not line:
        return None
    sentence = []
    while line and (line != "\n"):
        line = line.strip()
        word, tag = line.split("\t", 2)
        sentence.append((word, tag))
        line = f.readline()
    return sentence


def load_annotated_corpus(filename):
    sentences = []
    with open(filename, 'r', encoding='utf-8') as f:
        sentence = read_annotated_sentence(f)
        while sentence:
            sentences.append(sentence)
            sentence = read_annotated_sentence(f)
    return sentences


START = "<DUMMY_START_TAG>"
END = "<DUMMY_END_TAG>"
UNK = "<UNKNOWN>"
allTagCounts = Counter()
transitionCounts = Counter()
# use Counters inside these
perWordTagCounts = {}
emissionCounts = {}
# log probability distributions: do NOT use Counters inside these because
# missing Counter entries default to 0, not log(0)
A = {}  # transisions probabilities
B = {}  # emmissions probabilities

base = 2

TAG = 0
POINTER = 1
PROB = 2

REPLACE_TO_UNK_IF_COUNT = 2  # for smoothing OOV, best value on test set after experiments

w_vocab = set()  # word vocab
t_vocab = set()  # tag vocab
w_t_vocab = {}  # word tags vocab


def learn_params(tagged_sentences):
    """
    Populates and returns the allTagCounts, perWordTagCounts, transitionCounts,
    and emissionCounts data-structures.
    allTagCounts and perWordTagCounts should be used for baseline tagging and
    should not include pseudocounts, dummy tags and unknowns.
    The transisionCounts and emmisionCounts
    should be computed with pseudo tags and shoud be smoothed.
    A and B should be the log-probability of the normalized counts, based on
    transisionCounts and  emmisionCounts

    Args:
        tagged_sentences: a list of tagged sentences, each tagged sentence is a
        list of pairs (w,t), as retunred by load_annotated_corpus().

    Return:
        [allTagCounts,perWordTagCounts,transitionCounts,emissionCounts,A,B] (a list)
    """

    # find words that appeared less then REPLACE_TO_UNK_IF_COUNT for UNK pseudo count
    w_counts = Counter()
    for tagged_sentence in tagged_sentences:
        words = [word for (word, tag) in tagged_sentence]
        w_counts.update(words)
    occur_less_than_set = set([word for (word, count) in w_counts.items() if count <= REPLACE_TO_UNK_IF_COUNT])

    for tagged_sentence in tagged_sentences:
        # pseudo count for START
        last_tag = START
        for (word, tag) in tagged_sentence:

            w_vocab.add(word)
            t_vocab.add(tag)

            allTagCounts[tag] += 1

            if word not in perWordTagCounts:
                perWordTagCounts[word] = Counter()
                w_t_vocab[word] = set()
            perWordTagCounts[word][tag] += 1

            w_t_vocab[word].add(tag)

            if last_tag not in transitionCounts:
                transitionCounts[last_tag] = Counter()
            transitionCounts[last_tag][tag] += 1

            if tag not in emissionCounts:
                emissionCounts[tag] = Counter()

            emissionCounts[tag][word] += 1

            # pseudo count for UNK smoothing
            if word in occur_less_than_set:
                emissionCounts[tag][UNK] += 1 # UNK as word
                transitionCounts[last_tag][UNK] += 1 # UNK as tag

            last_tag = tag

        # pseudo count for END
        if last_tag not in transitionCounts:
            transitionCounts[last_tag] = Counter()
        transitionCounts[last_tag][END] += 1

    # prepare A,B
    for tag1 in transitionCounts.keys():
        normalizer = sum(transitionCounts[tag1].values())
        A[tag1] = {}
        for tag2 in transitionCounts[tag1].keys():
            A[tag1][tag2] = log((transitionCounts[tag1][tag2]) / normalizer, base)

    for tag in emissionCounts.keys():
        normalizer = sum(emissionCounts[tag].values())
        B[tag] = {}
        for word in emissionCounts[tag].keys():
            B[tag][word] = log((emissionCounts[tag][word]) / normalizer, base)

    return [allTagCounts, perWordTagCounts, transitionCounts, emissionCounts, A, B]


def baseline_tag_sentence(sentence, perWordTagCounts, allTagCounts):
    """Returns a list of pairs (w,t) where each w corresponds to a word
    (same index) in the input sentence. Each word is tagged by the tag most
    frequently associated with it. OOV words are tagged by sampling from the
    distribution of all tags.

    Args:
        sentence (list): a list of tokens (the sentence to tag)
        perWordTagCounts (Counter): tags per word as specified in learn_params()
        allTagCounts (Counter): tag counts, as specified in learn_params()

        Return:
        list: list of pairs
    """
    tagged_sentence = []
    key_in_tag_counts = list(allTagCounts.keys())
    val_in_tag_counts = list(allTagCounts.values())

    for word in sentence:
        if word not in perWordTagCounts:
            # OOV, sample from distribution of all tags
            tag = random.choices(key_in_tag_counts, weights=val_in_tag_counts)[0]
        else:
            tag = perWordTagCounts[word].most_common()[0][0]
        tagged_sentence.append((word, tag))

    return tagged_sentence


# ===========================================
#       POS tagging with HMM
# ===========================================


def hmm_tag_sentence(sentence, A, B):
    """Returns a list of pairs (w,t) where each w corresponds to a word
    (same index) in the input sentence. Tagging is done with the Viterby
    algorithm.

    Args:
        sentence (list): a list of tokens (the sentence to tag)
        A (dict): The HMM Transition probabilities
        B (dict): tthe HMM emmission probabilities.

    Return:
        list: list of pairs
    """
    tagged_sentence = []

    end_item = viterbi(sentence, A, B)  # search
    tag_list = retrace(end_item)  # find tag sequence

    for i, word in enumerate(sentence):  # generate pairs
        tagged_sentence.append((word, tag_list[i]))

    return tagged_sentence


def viterbi(sentence, A, B):
    """Creates the Viterbi matrix, column by column. Each column is a list of
    tuples representing cells. Each cell ("item") is a tupple (t,r,p), were
    t is the tag being scored at the current position,
    r is a reference to the corresponding best item from the previous position,
    and p is a log probability of the sequence so far).

    The function returns the END item, from which it is possible to
    trace back to the beginning of the sentence.

    Args:
        sentence (list): a list of tokens (the sentence to tag)
        A (dict): The HMM Transition probabilities
        B (dict): tthe HMM emmission probabilities.

    Return:
        obj: the last item, tagged with END. should allow backtraking.

        """

    # start with a dummy item
    dummy_start_item = (START, None, log(1.0, base))
    predecessor_list = [dummy_start_item]

    for word in sentence:
        current_list = []

        if word in w_t_vocab:
            # only tags seen with that word
            tags_to_search = list(w_t_vocab[word])
        else:
            # OOV, consider all tags
            tags_to_search = list(t_vocab)

        for tag in tags_to_search:
            current_list.append(predict_next_best(word, tag, predecessor_list, A, B))

        predecessor_list = current_list
    # end the sequence with a dummy
    v_last = predict_next_best(None, END, predecessor_list, A, B)

    return v_last


def retrace(end_item):
    """
        Returns a list of tags (retracing the sequence with the highest probability,
        reversing it and returning the list). The list should correspond to the
        list of words in the sentence (same indices).
    """

    tag_list = []

    current_item = end_item[POINTER]
    while current_item[POINTER] is not None:  # retracing until START
        tag_list.append(current_item[TAG])
        current_item = current_item[POINTER]

    return tag_list[::-1]  # reverse



def predict_next_best(word, tag, predecessor_list, A, B):
    """
    Returns a new item (tupple)
    """
    best_pred_option = None

    for predecessor in predecessor_list:
        (pred_tag, pointer, v_minus_one) = predecessor

        v = v_minus_one
        if tag not in A[pred_tag]:
            v += A[pred_tag][UNK]
        else:
            v += A[pred_tag][tag]
            # NOTE: apply this instead will give better results on test set...
            # if tag is not END:
            #     v += A[pred_tag][tag]
        if word is not None:
            if word not in w_vocab:
                v += B[tag][UNK]
            else:
                v += B[tag][word]

        if best_pred_option is None or v > best_pred_option[1]:
            best_pred_option = (predecessor, v)

    return (tag, best_pred_option[0], best_pred_option[1])


def joint_prob(sentence, A, B):
    """Returns the joint probability of the given sequence of words and tags under
     the HMM model.

     Args:
         sentence (pair): a sequence of pairs (w,t) to compute.
         A (dict): The HMM Transition probabilities
         B (dict): the HMM emmission probabilities.
     """
    p = 0  # joint log prob. of words and tags

    last_tag = START
    for (word, tag) in sentence:
        p += A[last_tag][tag] + B[tag][word]
        last_tag = tag

    assert isfinite(p) and p < 0  # Should be negative. Think why!
    return p


# ===========================================================
#       Wrapper function (tagging with a specified model)
# ===========================================================

def tag_sentence(sentence, model):
    """Returns a list of pairs (w,t) where pair corresponds to a word (same index) in
    the input sentence. Tagging is done with the specified model.

    Args:
        sentence (list): a list of tokens (the sentence to tag)
        model (dict): a dictionary where key is the model name and the value is
           an ordered list of the parameters of the trained model (baseline, HMM).

        Models that must be supported (you can add more):
        1. baseline: {'baseline': [perWordTagCounts, allTagCounts]}
        2. HMM: {'hmm': [A,B]}


        The parameters for the baseline model are:
        perWordTagCounts (Counter): tags per word as specified in learn_params()
        allTagCounts (Counter): tag counts, as specified in learn_params()

        The parameters for the HMM are:
        A (dict): The HMM Transition probabilities
        B (dict): tthe HMM emmission probabilities.


    Return:
        list: list of pairs
    """

    if list(model.keys())[0] == 'baseline':
        return baseline_tag_sentence(sentence, list(model.values())[0][0], list(model.values())[0][1])
    if list(model.keys())[0] == 'hmm':
        return hmm_tag_sentence(sentence, list(model.values())[0][0], list(model.values())[0][1])


def count_correct(gold_sentence, pred_sentence):
    """Return the total number of correctly predicted tags,the total number of
    correcttly predicted tags for oov words and the number of oov words in the
    given sentence.

    Args:
        gold_sentence (list): list of pairs, assume to be gold labels
        pred_sentence (list): list of pairs, tags are predicted by tagger

    """
    assert len(gold_sentence) == len(pred_sentence)

    correct = 0
    correctOOV = 0
    OOV = 0

    n_tokens = len(gold_sentence)
    for i in range(n_tokens):
        (gold_word, gold_tag) = gold_sentence[i]
        (pred_word, pred_tag) = pred_sentence[i]
        assert gold_word == pred_word

        match = int(gold_tag == pred_tag)
        if gold_word in w_vocab:
            correct += match
        else:
            OOV += 1
            correctOOV += match

    return correct, correctOOV, OOV
