import torch
#Check if GPU is available, and if it is, move the model into GPU memory.
def checkGPU():
    if torch.cuda.is_available():    
        device = torch.device("cuda")

        print('There are %d GPU(s) available.' % torch.cuda.device_count())

        print('We will use the GPU:', torch.cuda.get_device_name(0))
    else:
        print('No GPU available, using the CPU instead.')
        device = torch.device("cpu")
    
    return device
