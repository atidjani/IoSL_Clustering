from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect

from .forms import UploadDatasetForm
from .dataset import Dataset
from .runners import StscRunner

ds = None

# Uploader view
def UploadDatasetView(request):
    if request.method == 'POST':
        global ds
        form = UploadDatasetForm(request.POST, request.FILES)
        if form.is_valid() :
            ds = Dataset(request.FILES['file'])
            if ds.checkDataset() :
                return HttpResponseRedirect('/result')
            else :
                ds = None
                return HttpResponse("Error")
    else :
        form = UploadDatasetForm()
    return render(request, 'UploadDatasetTemplate.html', {'form': form})

# Result View
def ResultView(request) :
    # You fool. Go back to the Uploader View. The ds is not set
    if ds == None:
        return HttpResponseRedirect('/')

    # Execution of STSC
    stsc = StscRunner(ds.getFilePath(), 10, 6)
    output = stsc.run()
    return HttpResponse(output)
