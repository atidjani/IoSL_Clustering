import json

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect

from .forms import UploadDatasetForm
from .dataset import Dataset
from .runners import StscRunner


# Uploader view
def UploadDatasetView(request):
    if request.method == 'POST':
        form = UploadDatasetForm(request.POST, request.FILES)
        if form.is_valid() :
            ds = Dataset(request.FILES['file'], str(request.session.session_key)[:30])
            if ds.checkDataset() :
                # Set the session dataset
                request.session['ds'] = json.dumps(ds.__dict__)
                return HttpResponseRedirect('/result')
            else :
                ds = None
                return HttpResponse("Error")
    else :
        # Trick to avoid None session_key at the first request
        request.session['one'] = "One entry to rule them all and in the session bind them"
        form = UploadDatasetForm()
    return render(request, 'UploadDatasetTemplate.html', {'form': form})

# Result View
def ResultView(request) :
    # You fool. Go back to the Uploader View. The ds is not set
    if request.session.get('ds', None) == None :
        return HttpResponseRedirect('/')

    # Execution of STSC
    ds = type('Dataset', (object,), json.loads(request.session['ds']))
    stsc = StscRunner(ds.filePath, 10, 6)
    output = stsc.run()
    return render(request, 'ResultTemplate.html', output)
