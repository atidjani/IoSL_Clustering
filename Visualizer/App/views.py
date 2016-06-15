import os, math

from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from django.core.exceptions import ValidationError
from django.template import RequestContext

from .forms import *
from .models import Dataset
from .runners import StscRunner, OpticsRunner
from .noise import Noise


# Uploader view
def UploadDatasetView(request):
    if request.method == 'POST':
        form = UploadDatasetForm(request.POST, request.FILES)
        if form.is_valid() :
            ds = Dataset(data=request.FILES['file'].read())
            try :
                ds.full_clean()
            except ValidationError:
                return HttpResponseRedirect('/error')

            ds.save()
            request.session['ds'] = ds.id

            if form.cleaned_data['algorithm'] == 'STSC':
                return HttpResponseRedirect('/resultSTSC')
            elif form.cleaned_data['algorithm'] == 'OPTICSR' :
                return HttpResponseRedirect('/resultOPTICSR')
            elif form.cleaned_data['algorithm'] == 'OPTICSP' :
                return HttpResponseRedirect('/resultOPTICSP')
    else :
        # Trick to avoid None session_key at the first request
        request.session['noise'] = ''
        form = UploadDatasetForm()
    return render(request, 'UploadDatasetTemplate.html', {'form': form})

# Result View Optics - R
def ResultViewOPTICSR(request) :
    # You fool. Go back to the Uploader View. The ds is not set
    if request.session.get('ds', None) == None :
        return HttpResponseRedirect('/')

    # Get element
    dsId = request.session.get('ds')
    ds = Dataset.objects.get(id=dsId)

    if request.method == 'GET':
        # GET - First request prepare the form
        # Create Form
        form = ParametersOPTICSR(initial={'minPoints':15, 'eps':10, 'angle':120})
        # Set default parameters
        minPoints = 15
        eps = 10
        angle = math.cos(120)
    else :
        # POST - New calculation requested
        form = ParametersOPTICSR(request.POST)

        if form.is_valid() :
            minPoints = form.cleaned_data['minPoints']
            eps = form.cleaned_data['eps']
            angle = math.cos(form.cleaned_data['angle'])

            functions = form.cleaned_data['noiseFunctions']
            generateNoise = form.cleaned_data['generateNoise']

            if (functions != '' and generateNoise) :
                # Generate noise points
                noise = Noise(functions)
                noiseStr = noise.generatePoints()
                ds.noise = noiseStr
                ds.save()
            elif (functions == ''):
                ds.noise = ''
                ds.save()
        else :
            return HttpResponseRedirect('/resultOPTICSR')

    filePath = ds.writeFile() # Write dataset on disk
    optics = OpticsRunner(filePath, eps, minPoints, angle) # Execution of STSC
    output = optics.run('r')
    os.remove(filePath) # Delete File

    # Put the form in the output to display it
    output['form'] = form

    return render(request, 'ResultTemplateOPTICS.html', output)

# Result View Optics Python
def ResultViewOPTICSP(request) :
    return render(request, 'ResultTemplateOPTICS.html')

# Result View STSC
def ResultViewSTSC(request) :
    # You fool. Go back to the Uploader View. The ds is not set
    if request.session.get('ds', None) == None :
        return HttpResponseRedirect('/')

    # Get element
    dsId = request.session.get('ds')
    ds = Dataset.objects.get(id=dsId)

    if request.method == 'GET':
        # GET - First request prepare the form
        # Create Form
        form = ParametersSTSC(initial={'numClusters':10, 'k':7})
        # Set default parameters
        numClusters = 10
        k = 6
    else :
        # POST - New calculation requested
        form = ParametersSTSC(request.POST)
        if form.is_valid() :
            numClusters = form.cleaned_data['numClusters']
            k = form.cleaned_data['k'] - 1

            functions = form.cleaned_data['noiseFunctions']
            generateNoise = form.cleaned_data['generateNoise']

            if (functions != '' and generateNoise) :
                # Generate noise points
                noise = Noise(functions)
                noiseStr = noise.generatePoints()
                ds.noise = noiseStr
                ds.save()
            elif (functions == ''):
                ds.noise = ''
                ds.save()
        else :
            return HttpResponseRedirect('/resultSTSC')

    filePath = ds.writeFile() # Write dataset on disk
    stsc = StscRunner(filePath, numClusters, k) # Execution of STSC
    output = stsc.run()
    os.remove(filePath) # Delete File

    # Put the form in the output to display it
    output['form'] = form

    return render(request, 'ResultTemplateSTSC.html', output)


# Show the error page
def ErrorView(request) :
    return render(request, 'ErrorPage.html', {'error': 'Not supported dataset'})

