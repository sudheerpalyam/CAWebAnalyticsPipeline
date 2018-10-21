# Data Engineer Work Sample Test

In this challenge, we'll ask you to build a mini data pipeline, write a little SQL and answer a few questions. We think this test is a good approximation to the type of work you'll be doing as a data engineer at Culture Amp. We thank you for taking the time to work on this, and wish you the best of luck!

Please keep the following things in mind:

- If the content of the test doesn't interest you, we suggest you do *not* complete it. It probably means this isn't the right role for you, and that's totally fine. 
- Treat the test as if it was a piece of work you'd undertake while working at Culture Amp. We understand you have life commitments, so let us know the trade-offs you needed to make to get the work done.
- Where there is ambiguity, make reasonable assumptions and note them in your submission.
- There is no time requirement; you can spend as little or as much time as you like on the test. As a guide, it will probably take more than 1 hour and less than 3.

There are 3 parts to this test:

- Build a data pipeline
- Write SQL, design a database schema
- Answer questions about the data engineering landscape

## Build a Data Pipeline

For this challenge, please reference the `data.csv` for the data, and the `data-description.md` for a description of the data. We ask that you build a data pipeline which ingests, transforms and loads data from the given CSV file. This pipeline has two steps: one for batch processing and another for stream processing.

Beyond meeting the minimum requirements, it’s up to you where you want to focus. We don’t expect a fully-finished, production-quality data pipeline; rather, we’re happy for you to focus on whatever areas you feel best showcase your skills.

### Batch Processing:

- Read in the included CSV file(s)
- Augment the data with the following additional fields:
    - `utm_source`
    - `utm_medium`
    - `path`
- Filter out questionable data from the data set (use your judgement)
- Write data out as JSONL to a file, stdout or to a stream, one record at a time

### Micro-batch or Stream Processing:

- Watch for the JSONL data emitted from the previous task
- When the number of events reach 1000, output the events to a JSON file with the following properties:
    - the file name should be the batch number. For example, the second 1000 records will go into a file called 2.json
    - Each file should contain the following key-value pairs:
        - `min date` - the smallest date in the batch
        - `max date` - the smallest date in the batch   
        - `unique users` - the number of unique users in the batch

### Recommendations

- Ensure you are building a pipeline with more than one step.
- Feel free to use frameworks and libraries, but keep in mind that we are looking for something that demonstrates that you can write ETL code, not just wire up a framework.
- Include a README file with clear build instructions that we can follow.
- Include in your README any other details you would like to share, such as tradeoffs you chose to make, what areas of the problem you chose to focus on and the reasons for your design decisions.
- We like tests.


## Star Schema and SQL

In this question, we'll work with the data outputted from the first step of your pipeline: the original CSV, plus path, source and medium columns.

Our web analytics team needs your help making sense of the data. They now want to be able to work with the data using their favourite BI tool. Examples of questions they tend to ask are:

- Show the number of unique users by page path
- Count the sessions that had "Google" as their source (`utm_source`)
- Is there a meaningful difference in pageviews per session with different browsers

Please answer the following questions:

- Write a SQL query to sessionise the data, adding a session_id column that is unique for each session. A session is a set of pageviews performed by the same user without a large time gap between them. You can assume if a user hasn't had a pageview in the previous 20 minutes, the current pageview denotes a new session.
- Propose a dimensional model--fact and dimension table(s)--that would offer the users a good self-service experience in their favourite BI tool.
- Our digital analyst wants to combine one of your stars with another star schema that has a dimension in common with your above solution to answer a complex question. What advice would you give them on how to join data from two star schemas?

## Data Engineering Landscape

The data management space is filled with tools. A great skill is to keep up with common and emerging technologies and understand where they sit in the data ecosystem, and how they add value. 

Please consider the following unnamed categories and some example tools.

|Category A |Category B         |Category C   |Category D|
|-----------|-------------------|-------------|----------|
|Redshift   |Snowflake          |Apache Sqoop |ORC       |
|Vertica    |Presto             |Apache Nifi  |Avro      |
|Big Query  |Redshift Spectrum  |Embulk       |Parquet   |

For every category, please answer the following questions:

1. Give the category a meaninful name
2. Add a fourth entry to the category
3. Select a tool in the category and then describe a use case that it is most fit for. You may use the item you added in the previous question.
    - Why is this tool a good fit for the use case you selected?
    - When might another tool be better?
4. Add another category to the table above with an example of a tool in that category.

## Optional Additional Questions

You will not be assessed on these questions, and they are completely optional:

- How long did this test take you?
- How can we improve this test?
- Is there anything you think we should have asked but did not?

## Submitting Your Solution

Assuming you use Git to track changes to your code, when you’re ready to submit your solution, please use git bundle to package up a copy of your repository (with complete commit history) as a single file and send it to us as an email attachment.

```
git bundle create data-engineer-test.bundle master
```

We’re looking forward to your innovative solutions!
