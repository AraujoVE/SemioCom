import os
import json
def savePreTrained(model, tokenizer,training_stats):
    output_dir = './model_save/'

    #Create output directory if needed
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    print("Saving model to %s" % output_dir)

    # Save a trained model, configuration and tokenizer using `save_pretrained()`. They can then be reloaded using `from_pretrained()`
    model_to_save = model.module if hasattr(model, 'module') else model  # Take care of distributed/parallel training
    model_to_save.save_pretrained(output_dir)
    tokenizer.save_pretrained(output_dir)
    jsonStats = json.dumps(dict([[tuple("Batch {0}".format(i),training_stats[i])] for i in range(len(training_stats))]),indent=4)
    print(jsonStats,file=open("{0}batchStats".format(jsonStats)))
    # Good practice: save your training arguments together with the trained model
    #torch.save(args, os.path.join(output_dir, 'training_args.bin'))

